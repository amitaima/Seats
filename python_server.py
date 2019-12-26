# -*- coding: utf-8 -*-
from __future__ import print_function
import atexit
import socket
import sqlite3
import threading
from datetime import datetime, date, time, timedelta
import time
import schedule
import pickle
import os.path
import sys
from googleapiclient.discovery import build
from google_auth_oauthlib.flow import InstalledAppFlow
from google.auth.transport.requests import Request
from gsmmodem.modem import GsmModem

# If modifying these scopes, delete the file token.pickle.
SCOPES = ['https://www.googleapis.com/auth/spreadsheets.readonly']

# The ID and range of a sample spreadsheet.
SPREADSHEET_ID = '1D_E3p_GRtuh08MsN0fo2lXFR-94YIvQN1MKg6MyQVUk'
RANGE_NAME = 'Sheet1!A1:A'
RANGE_NAME_USERS = 'Sheet2!A2:C'

PORT = 'COM15'
BAUDRATE = 115200
PIN = None # SIM card PIN (if any)

HOSTCLIENT = '0.0.0.0'
PORTCLIENT = 443

#inserts employees from given file of employees into the table "employees"
def user_insertion():
    emp_file = open("testFileUsers.txt").read()
    splited = emp_file.split("|")
    numberOfBk = len(db.execute("SELECT * FROM batei_kneset").fetchall())
    for j in range(0,numberOfBk):
        a=splited[j].splitlines()
        # for i in range(0,len(a)-1):
        #     a[i] = a[i].split("\t")
        BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(j) + "'").fetchone()
        print(BKname[0])
        query = "DELETE FROM '"+ str(BKname[0]) +"' "
        db.execute(query)
        query = "INSERT INTO '"+str(BKname[0])+"' VALUES (?, ?, ? , ?)"
        status = 0
        # while(a[i] != None):
        print (a[208])
        print(len(a))
        for i in range(0,len(a)):
            a[i] = a[i].split(" ")
            # print(a[i][1].decode("iso-8859-8"))
            # name = a[i][1].decode("iso-8859-8")
            if len(a[i])==3:
                name = a[i][1]
                if "פנוי" in name:
                    phoneNumber = "0000000000"
                else:
                    phoneNumber = "+" + a[i][2]
            else:
                name = a[i][1] + " " + a[i][2]
                if "פנוי" in name:
                    phoneNumber = "0000000000"
                else:
                    phoneNumber = "+" + a[i][3]
            id = int(a[i][0])
            db.execute(query, (id, name, phoneNumber, status))
            # print i
            # i+=1
        db.commit()
        print ("Added employees successfully BK - " + str(j))

def get_statuses(BKid):
    # print "Entered get_statuses"
    # if date.today().weekday() == 3:
    print ("Getting statuses")
    BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(BKid) + "'").fetchone()
    query = "SELECT id FROM '" + str(BKname[0]) + "' WHERE status = 1"
    all = db.execute(query)
    allEmps = all.fetchall()
    finalReturn=""
    if allEmps != None:
        for i in range(0,len(allEmps)):
            send = str(allEmps[i][0])
            finalReturn = finalReturn+send+" "
        return (finalReturn)
        #need to send to client

def reset_statuses():
    # print "Entered reset_statuses"
    # if date.today().weekday() == 6:
    numberOfBk = len(db.execute("SELECT * FROM batei_kneset").fetchall())
    for i in range(0,numberOfBk):
        BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(i) + "'").fetchone()
        print(BKname[0])
        query = "UPDATE '"+str(BKname[0])+"' SET status = 0"
        c.execute(query)
    db.commit()

def get_users(BKid):
    BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(BKid) + "'").fetchone()
    query = "SELECT * FROM '"+str(BKname[0])+"'"
    all = db.execute(query)
    allEmps = all.fetchall()
    finalReturn=""
    if allEmps != None:
        for i in range(0,len(allEmps)):
            send = str(allEmps[i][0]) + " " + allEmps[i][1] + " " + allEmps[i][2] + " " + str(allEmps[i][3])
            finalReturn = finalReturn+send+","
        print(finalReturn)
        return finalReturn

def get_user_from_phone(phoneNumber, BKid):
    BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(BKid) + "'").fetchone()
    query = "SELECT * FROM '"+str(BKname[0])+"' WHERE phoneNumber = '" + phoneNumber + "'"
    user = db.execute(query)
    user = user.fetchall()
    finalReturn=""
    if user != None:
        for i in range(0,len(user)):
            send = str(user[i][0]) + " " + user[i][1] + " " + user[i][2] + " " + str(user[i][3])
            finalReturn = finalReturn+send+","
        return finalReturn

def get_seatid_from_phone(phoneNumber, BKid):
    BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(BKid) + "'").fetchone()
    query = "SELECT id FROM '"+str(BKname[0])+"' WHERE phoneNumber = '" + phoneNumber + "'"
    user = db.execute(query)
    user = user.fetchall()
    finalReturn=""
    if user != None:
        for i in range(0,len(user)):
            send = str(user[i][0])
            finalReturn = finalReturn+send+" "
        return(finalReturn)

def get_all_numbers():
    numberOfBk = len(db.execute("SELECT * FROM batei_kneset").fetchall())
    for i in range(0,numberOfBk-1):
        BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(i) + "'").fetchone()
        query = "SELECT phoneNumber FROM '"+str(BKname[0])+"'"
        phoneNumbers = db.execute(query)
        phoneNumbers = phoneNumbers.fetchall()
        finalReturn=""
        if phoneNumbers != None:
            for i in range(0,len(phoneNumbers)):
                if "+972" in phoneNumbers[i][0]:
                    send = str(phoneNumbers[i][0])
                    finalReturn = finalReturn+send+" "
            finalfinalReturn = finalfinalReturn+finalReturn+" "
    return (finalfinalReturn)

def send_msg(phoneNumber):
    sendingNumber = phoneNumber
    if "549766158" in phoneNumber:
        phoneNumber = "+972549766158"
    elif "587766185" in phoneNumber:
        phoneNumber = "+972526565732"
    elif "549766185" in phoneNumber:
        phoneNumber = "+972546621037"
    else:
        phoneNumber = "+972523793259"
    numberOfBk = len(db.execute("SELECT * FROM batei_kneset").fetchall())
    for i in range(0,numberOfBk-1):
        BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(i) + "'").fetchone()
        exists = db.execute("SELECT EXISTS(SELECT name FROM '"+str(BKname[0])+"' WHERE phoneNumber = '"+phoneNumber+"')")
        if exists!=None:
            BKid = i
            break
    numOfSeats = str(len(get_seatid_from_phone(phoneNumber,BKid).split(" "))-1)
    message="זוהי הודעה מגבאי בית הכנסת : נשמח אם תוכל להחזיר הודעה עם מספר המושבים הפנויים שמוקצים לך ולמשפחתך בשבת הקרובה." + "\nמקסימום: " + numOfSeats
    print("sending message to " + sendingNumber)
    modem.sendSms(sendingNumber, message)

#Sends a message to every user in the list
def send_all():
    open("AnsweredPhones.txt", "w").close()
    # send_msg("+972549766158")
    # send_msg("+972587766185")
    # modem.deleteMultipleStoredSms()
    print ("Entered send_all")
    if date.today().weekday() == 3: #3=thursday, goes from moday(0) to sunday(6)
        print("Today should send")
        send_msg("+972549766158")
        send_msg("+972587766185")
        send_msg("+972549766185")
        modem.deleteMultipleStoredSms()
        # phoneNumbers = get_all_numbers().split(" ")
        # for i in range(0,len(phoneNumbers)):
        #     send_msg(phoneNumbers[i])
    else:
        print("Today isn't the day")

#Sends message a second time to every number who didn't return a normal answer.
def send_all_second_time():
    print ("Entered send_all_second_time")
    if date.today().weekday() == 2 or 1==1: #4=friday, goes from moday(0) to sunday(6)
        print("Today should send second time")
        with open("AnsweredPhones.txt") as f:
            lineList = f.read()
        # phoneNumbers = get_all_numbers().split(" ")
        phoneNumbers = ["+972549766158","+972549766185","+972587766185"]
        print(lineList)
        for i in range(0,len(phoneNumbers)): #Check witch number has already responded and send to the remaining numbers.
            if phoneNumbers[i] in lineList: #str(phoneNumbers[i])
                print("Already sent to " + phoneNumbers[i])
                # None
            else:
                print("should send - " + phoneNumbers[i])
                send_msg(phoneNumbers[i])
        open('AnsweredPhones.txt', 'w').close()
    else:
        print("Today isn't the day")
    open("AnsweredPhones.txt", "w").close()

def schedule_all():
    print("Entered Schedule send")
    schedule.every().thursday.at("18:00").do(send_all)
    schedule.every().friday.at("08:00").do(send_all_second_time)
    schedule.every().thursday.at("10:00").do(update_users)
    schedule.every().saturday.at("23:00").do(reset_statuses)
    # schedule.every().thursday.at("21:25").do(send_all)
    # schedule.every().thursday.at("21:29").do(send_all_second_time)
    # schedule.every().thursday.at("14:03").do(reset_statuses)
    while True:
        schedule.run_pending()
        time.sleep(60)

def set_statuses_from_phone(phoneNumber,number, BKid):
    BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(BKid) + "'").fetchone()
    seats = get_seatid_from_phone(phoneNumber, BKid).split(" ")
    for i in range(0,len(seats)):
        if i< number:
            query = "UPDATE '"+str(BKname[0])+"' SET status = 1 WHERE id = '" + seats[i] + "'"
            db.execute(query)
        else:
            query = "UPDATE '"+str(BKname[0])+"' SET status = 0 WHERE id = '" + seats[i] + "'"
            db.execute(query)
    print("set")
    db.commit()

def handleSms(sms):
    print(u'== SMS message received ==\nFrom: {0}\nTime: {1}\nMessage:\n{2}\n'.format(sms.number, sms.time, sms.text))
    #check if received sms content is a number
    phoneNumber = sms.number
    if "549766158" in phoneNumber:
        phoneNumber = "+972549766158"
    elif "584966113" in phoneNumber:
        phoneNumber = "+972526565732"
    elif "549766185" in phoneNumber:
        phoneNumber = "+972546621037"
    else:
        phoneNumber = "+972523793259"
    numberOfBk = len(db.execute("SELECT * FROM batei_kneset").fetchall())
    for i in range(0,numberOfBk-1):
        BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(i) + "'").fetchone()
        exists = db.execute("SELECT EXISTS(SELECT name FROM '"+str(BKname[0])+"' WHERE phoneNumber = '"+phoneNumber+"')")
        if exists!=None:
            BKid = i
            break
    # phoneNumber = "+972523793259"
    numOfSeats = str(len(get_seatid_from_phone(phoneNumber, BKid).split(" "))-1) #sms.number
    # numOfSeats = 2
    print(numOfSeats)
    print("sms.text = " + sms.text)
    if sms.text.isdigit() == True:
        if int(sms.text)<=int(numOfSeats):
            sms.reply(sms.text + u" מקומות עודכנו בהצלחה!")
            with open("AnsweredPhones.txt", "a") as myFile:
                myFile.write(sms.number + " ")
            set_statuses_from_phone(phoneNumber,int(sms.text),BKid) #sms.number
            print(get_statuses(BKid))
            print("done")
        else:
            sms.reply(u"תגובתך אינה תואמת את המבוקש, אנא נסה שוב.")
    else:
        sms.reply(u"תגובתך אינה תואמת את המבוקש, אנא נסה שוב.")
    modem.deleteMultipleStoredSms()

def main():
    # sendsms("+972549766158", "Hello")
    print('Initializing modem...')
    # Uncomment the following line to see what the modem is doing:
    # logging.basicConfig(format='%(levelname)s: %(message)s', level=logging.DEBUG)
    # modem = GsmModem(PORT)
    # modem.connect()
    # modem.smsc = "+972586279099"
    # modem.sendSms("+972549766158", "before")
    # modem.close()
    numOfSeats = 4
    message="זוהי הודעה מגבאי בית הכנסת : נשמח אם תוכל להחזיר הודעה עם מספר המושבים הפנויים שמוקצים לך ולמשפחתך בשבת הקרובה." + "\nמקסימום: " + str(numOfSeats)
    modem = GsmModem(PORT, BAUDRATE, smsReceivedCallbackFunc=handleSms)
    modem.smsTextMode = False
    modem.connect(PIN)
    # modem.smsc = "+972586279099" #golan telecom
    modem.smsc = "+972521100059" #cellcom
    # modem.sendSms("+972549766158", "שלום מדבר אמיתי מלכה תוכל להגיב לי בבקשה?")
    modem.sendSms("+972587766185", message)
    # modem.smsc = "+972586279099"
    print('Waiting for SMS message...')
    try:
        modem.rxThread.join(2**31) # Specify a (huge) timeout so that it essentially blocks indefinitely, but still receives CTRL+C interrupt signal
    finally:
        modem.close()

def update_messages(BKid):
    """Shows basic usage of the Sheets API.
    Prints values from a sample spreadsheet.
    """
    creds = None
    letter = chr(int(BKid)*2+65) #65='A'
    print(letter)
    RANGE_NAME = 'הודעות גבאים!' + letter + "2:" + letter
    # The file token.pickle stores the user's access and refresh tokens, and is
    # created automatically when the authorization flow completes for the first
    # time.
    if os.path.exists('token.pickle'):
        with open('token.pickle', 'rb') as token:
            creds = pickle.load(token)
    # If there are no (valid) credentials available, let the user log in.
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            flow = InstalledAppFlow.from_client_secrets_file(
                'credentials.json', SCOPES)
            creds = flow.run_local_server()
        # Save the credentials for the next run
        with open('token.pickle', 'wb') as token:
            pickle.dump(creds, token)

    service = build('sheets', 'v4', credentials=creds)

    # Call the Sheets API
    sheet = service.spreadsheets()
    result = sheet.values().get(spreadsheetId=SPREADSHEET_ID,
                                range=RANGE_NAME).execute()
    values = result.get('values', [])
    finalReturn=""
    if not values:
        return ('000')
    else:
        for row in values:
            finalReturn = finalReturn+row[0]+"\n"
            # Print columns A and E, which correspond to indices 0 and 4.
            # print('%s' % (row[0]))
        return (finalReturn)

def update_users():
    """Shows basic usage of the Sheets API.
    Prints values from a sample spreadsheet.
    """
    tableSize = len(db.execute("SELECT id FROM batei_kneset").fetchall())
    letter = chr(tableSize*3+64) #65='A'
    RANGE_NAME_USERS = 'מפת בתי הכנסת!A3:' + letter
    creds = None
    # The file token.pickle stores the user's access and refresh tokens, and is
    # created automatically when the authorization flow completes for the first
    # time.
    if os.path.exists('token.pickle'):
        with open('token.pickle', 'rb') as token:
            creds = pickle.load(token)
    # If there are no (valid) credentials available, let the user log in.
    if not creds or not creds.valid:
        if creds and creds.expired and creds.refresh_token:
            creds.refresh(Request())
        else:
            flow = InstalledAppFlow.from_client_secrets_file(
                'credentials.json', SCOPES)
            creds = flow.run_local_server()
        # Save the credentials for the next run
        with open('token.pickle', 'wb') as token:
            pickle.dump(creds, token)

    service = build('sheets', 'v4', credentials=creds)

    # Call the Sheets API
    sheet = service.spreadsheets()
    result = sheet.values().get(spreadsheetId=SPREADSHEET_ID,
                                range=RANGE_NAME_USERS).execute()
    values = result.get('values', [])
    finalReturn=""
    if not values:
        return ('000')
    else:
        print("tablesize: " + str(tableSize))
        for i in range(0,tableSize):
            for row in values:
                finalReturn = finalReturn+row[0+(i*3)]+ " " + row[1+(i*3)] + " " + row[2+(i*3)] + "\n"
                # print (finalReturn)
                # Print columns A, B and C, which correspond to indices 0, 1 and 2.
                # print('%s, %s, %s' % (row[0], row[1], row[2]))
            finalReturn =finalReturn+"|"
        # print(finalReturn)
        temp_file = open("testFileUsers.txt").read()
        if temp_file.decode('utf-8') != finalReturn:
            with open('testFileUsers.txt', 'w') as testFile:
                testFile.write(finalReturn.encode('utf-8'))
            user_insertion()
        # return (finalReturn)

def manage_data(conn,addr):
    while 1:
            print ("recieving...")
            data = conn.recv(25)
            if not data:
                # client closed the connection
                break
            print (data)
            if "get statuses" in data:
                print ("received get statuses command")
                statuses = get_statuses(data.split(" ")[2])
                print(statuses)
                print(str(len(statuses)))
                if len(str(len(statuses))) == 1:
                    conn.send("0" + str(len(statuses)))
                else:
                    conn.send(str(len(statuses)))
                data = conn.recv(12)
                print("data: " + data)
                if "received" in data:
                    conn.send(statuses) #sends list of seat id's where status = 1
                break
            elif "get messages" in data:
                print ("received get messages command")
                messages = update_messages(data.split(" ")[2])
                print(len(messages))
                conn.send(str(len(messages)*2))
                data = conn.recv(12)
                print("data: " + data)
                if "received" in data:
                    messages = messages.encode('utf-8')
                    messages = messages+messages
                    print (messages)
                    conn.send(messages) #sends list of messages
                else:
                    conn.send("000")
                break
            elif "get updates" in data:
                print ("received get updates command")
                allUsers = open("testFileUsers.txt").read()
                splitUsers = allUsers.split("|")
                users=splitUsers[int(data.split(" ")[2])]
                users = users[:-1]
                userLines = users.splitlines()
                i=0
                joinedLines=''
                while (i+20)<len(userLines):
                    joinedLines='\n'.join(userLines[i:i+20])+'\n'
                    print(str(len(str(len(joinedLines)))))
                    conn.send(str(len(str(len(joinedLines)*2+1))))
                    data = conn.recv(12)
                    print(str(len(joinedLines)))
                    conn.send(str(len(joinedLines)*2+1))
                    data = conn.recv(12)
                    print("data: " + data)
                    if "received" in data:
                        messages = joinedLines
                        messages = messages+"|"+messages
                        print ("--- Updating Users ---")
                        conn.send(messages) #sends list of users
                        conn.recv(12)
                        print("i = " + str(i))
                        i=i+20
                joinedLines='\n'.join(userLines[i:i+20])+'\n'
                print(str(len(str(len(joinedLines)))))
                conn.send(str(len(str(len(joinedLines)))))
                data = conn.recv(12)
                print(str(len(joinedLines)))
                conn.send(str(len(joinedLines)*2+1))
                data = conn.recv(12)
                print("data: " + data)
                if "received" in data:
                    messages = joinedLines
                    messages = messages+"|"+messages
                    print ("--- Updating Users ---")
                    conn.send(messages) #sends list of users
                    conn.recv(12)
                conn.send('0')
                conn.recv(12)
            elif "get password" in data:
                print ("received get password command")
                query = "SELECT password FROM batei_kneset WHERE id = '" + data.split(" ")[2] + "'"
                password = db.execute(query)
                password = password.fetchone()
                conn.send(str(password[0]))
            else:
                print ("error: Unknown command")
                conn.send("Unknown command")
                time.sleep(1)
    db.commit()

if __name__ == '__main__':
    db = sqlite3.connect('users.db', check_same_thread=False)
    db.text_factory = str
    c = db.cursor()
    # update_users()
    reset_statuses()
    # update_users()
    # user_insertion()
    threading.Thread(target = schedule_all).start()

    modem = GsmModem(PORT, BAUDRATE, smsReceivedCallbackFunc=handleSms)
    modem.smsTextMode = False
    modem.connect(PIN)
    modem.deleteMultipleStoredSms()
    # send_all()
    # send_msg("+972549766158")
    # modem.deleteMultipleStoredSms()
    # modem.smsc = "+972521100059" #cellcom


    # threading.Thread(target = schedule_all).start()

    while 1:
        s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        print ("Socket created")
        try:
            s.bind((HOSTCLIENT,PORTCLIENT))
        except socket.error as msg:
            print ("Bind failed. Error code : " + str(msg) + " message " + msg[1])
            sys.exit()
        print ("Socket bind complete")
        s.listen(10)
        print ("Socket now listening")
        while True:
            conn, addr = s.accept() # sever connection
            print ("Connected with " + addr[0] + ":" + str(addr[1]))
            messageThread = threading.Thread(target = manage_data, args = (conn, addr,))
            messageThread.start()


    print('Waiting for SMS message...')
    try:
        modem.rxThread.join(2**31) # Specify a (huge) timeout so that it essentially blocks indefinitely, but still receives CTRL+C interrupt signal
    finally:
        modem.close();

    db.commit()
    # s.close()
    atexit.register(db.close)
