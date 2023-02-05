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
import unicodedata

# If modifying these scopes, delete the file token.pickle.
SCOPES = ['https://www.googleapis.com/auth/spreadsheets.readonly']

# The ID and range of a sample spreadsheet.
SPREADSHEET_ID = '1D_E3p_GRtuh08MsN0fo2lXFR-94YIvQN1MKg6MyQVUk'
RANGE_NAME = 'Sheet1!A1:A'
RANGE_NAME_USERS = 'Sheet2!A2:C'

PORT = 'COM34'
BAUDRATE = 115200
PIN = None # SIM card PIN (if any)

HOSTCLIENT = '0.0.0.0'
PORTCLIENT = 443

#inserts employees from given file of employees into the table "employees"
def user_insertion_BK():
    emp_file = open("BkUsersFile.txt").read()
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
            a[i] = a[i].split("\t")
            # print(a[i][1].decode("iso-8859-8"))
            # name = a[i][1].decode("iso-8859-8")
            name = a[i][1]
            if "פנוי" in name:
                phoneNumber = "0000000000"
            else:
                phoneNumber = "+" + a[i][2]
            id = int(a[i][0])
            db.execute(query, (id, name, phoneNumber, status))
            # print i
            # i+=1
        db.commit()
        print ("Added employees successfully BK - " + str(j))

def user_insertion_PL():
    emp_file = open("PlUsersFile.txt").read()
    splited = emp_file.split("|")
    numberOfBk = len(db.execute("SELECT * FROM parking_lots").fetchall())
    for j in range(0,numberOfBk):
        a=splited[j].splitlines()
        # for i in range(0,len(a)-1):
        #     a[i] = a[i].split("\t")
        PLname = db.execute("SELECT name FROM parking_lots WHERE id= '" + str(j) + "'").fetchone()
        print(PLname[0])
        query = "DELETE FROM '"+ str(PLname[0]) +"' "
        db.execute(query)
        query = "INSERT INTO '"+str(PLname[0])+"' VALUES (?, ?, ?, ?, ?)"
        status = 0
        # while(a[i] != None):
        print(len(a))
        for i in range(0,len(a)):
            a[i] = a[i].split("\t")
            id = i + 1
            # print ("a[0] - " + a[0])
            # print("a[1] - " + a[1])
            if "_" in a[i][0]: #No parking in line
                print ("No Parking in line")
                name = a[i][1]
                phoneNumber = "+" + a[i][2]
                db.execute(query, (id, name, phoneNumber, None, None))
            elif "_" in a[i][1]: #No user in line
                parkingName = a[i][0]
                db.execute(query, (id, None, None, status, parkingName))
            else: #Both in line
                name = a[i][1]
                phoneNumber = "+" + a[i][2]
                parkingName = a[i][0]
                db.execute(query, (id, name, phoneNumber, status, parkingName))
                # print i
                # i+=1
        db.commit()
        print ("Added employees successfully PL - " + str(j))

def get_statuses(table_name, BKid):
    print ("Getting statuses")
    BKname = db.execute("SELECT name FROM '"+table_name+"' WHERE id= '" + str(BKid) + "'").fetchone()
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

def reset_statuses(table_name):
    numberOfBk = len(db.execute("SELECT * FROM '"+table_name+"'").fetchall())
    for i in range(0,numberOfBk):
        BKname = db.execute("SELECT name FROM '"+table_name+"' WHERE id= '" + str(i) + "'").fetchone()
        print(BKname[0])
        query = "UPDATE '"+str(BKname[0])+"' SET status = 0"
        c.execute(query)
    db.commit()
    if table_name == "parking_lots":
        if int(datetime.today().day) == 1:
            for k in range(0, numberOfBk):  # Delete all files for new month
                fileName = "monthly_update_" + str(k) + ".txt"
                with open(fileName, 'w') as currFile:
                    currFile.write("")
        if int(datetime.today().weekday()) == 6:
            print("deleting week")
            for k in range(0, numberOfBk):  # Delete all files for new week
                fileName = "weekly_update_" + str(k) + ".txt"
                with open(fileName, 'w') as currFile:
                    currFile.write("")

        for j in range(0, numberOfBk): # Add todays date to all files
            fileNameM = "monthly_update_" + str(j) + ".txt"
            fileNameW = "weekly_update_" + str(j) + ".txt"
            with open(fileNameM, 'a+') as currFile:
                currFile.write("\n" + datetime.today().strftime('%Y-%m-%d') + "\n")
            with open(fileNameW, 'a+') as currFile:
                currFile.write("\n" + datetime.today().strftime('%Y-%m-%d') + "\n")

        allLines = open("takenSpots.txt").readlines()
        for i in range(0, len(allLines)): # Put each line in the correct file
            splitedLine = allLines[i].split("\t")
            PLid = splitedLine[0].rstrip()
            fileNameM = "monthly_update_" + PLid + ".txt"
            fileNameW = "weekly_update_" + PLid + ".txt"
            file = open(fileNameM, 'a+')
            file.write("")
            with open(fileNameM, 'a+') as currFile:
                currFile.write(" ".join(allLines[i].split("\t")[1:]))
            file = open(fileNameW, 'a+')
            file.write("")
            with open(fileNameW, 'a+') as currFile:
                currFile.write(" ".join(allLines[i].split("\t")[1:]))

        with open("takenSpots.txt", 'w') as dayFile: # Delete day file
            dayFile.write("")



def get_users(table_name, BKid):
    BKname = db.execute("SELECT name FROM '"+table_name+"' WHERE id= '" + str(BKid) + "'").fetchone()
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

def get_user_from_phone(table_name, phoneNumber, BKid):
    BKname = db.execute("SELECT name FROM '"+table_name+"' WHERE id= '" + str(BKid) + "'").fetchone()
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

def get_all_numbers(table_name):
    numberOfBk = len(db.execute("SELECT * FROM '"+table_name+"'").fetchall())
    for i in range(0,numberOfBk-1):
        BKname = db.execute("SELECT name FROM '"+table_name+"' WHERE id= '" + str(i) + "'").fetchone()
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

def get_parking_name_from_id(BKname,id):
    query = "SELECT parkingName FROM '" + str(BKname[0]) + "' WHERE id = '" + str(id) + "'"
    name = db.execute(query)
    name = name.fetchone()[0]
    return(name)

def get_id_from_parking_name(BKname,PLname):
    query = "SELECT id FROM '" + str(BKname[0]) + "' WHERE parkingName = '" + PLname + "'"
    name = db.execute(query)
    name = name.fetchone()[0]
    return(name)

def get_name_from_phone(table_name, phoneNumber, BKid):
    BKname = db.execute("SELECT name FROM '"+table_name+"' WHERE id= '" + str(BKid) + "'").fetchone()
    query = "SELECT name FROM '"+str(BKname[0])+"' WHERE phoneNumber = '" + phoneNumber + "'"
    user = db.execute(query)
    user = user.fetchone()[0]
    return user

def get_next_parking(BKid,phoneNumber):
    BKname = db.execute("SELECT name FROM parking_lots WHERE id= '" + str(BKid) + "'").fetchone()
    query = "SELECT id FROM '"+str(BKname[0])+"' WHERE status = 0"
    id = db.execute(query)
    id = id.fetchone()
    if id:
        id=id[0]
        name = get_name_from_phone("parking_lots", phoneNumber, BKid)
        with open("takenSpots.txt", "a+") as myFile:
            myFile.write(str(BKid) + "\t" + name + "\t" + get_parking_name_from_id(BKname,id) + "\t" + str(phoneNumber) +"\n")
        query = "UPDATE '"+str(BKname[0])+"' SET status = 1 WHERE id = '" + str(id) + "'"
        db.execute(query)
        db.commit()
        return str(get_parking_name_from_id(BKname, id))
    else:
        return 0

def reset_parking_status(BKid,parkingId):
    BKname = db.execute("SELECT name FROM parking_lots WHERE id= '" + str(BKid) + "'").fetchone()
    query = "UPDATE '"+str(BKname[0])+"' SET status = 0 WHERE parkingName = '" + parkingId + "'"
    db.execute(query)
    db.commit()


def send_msg(table_name, phoneNumber):
    # sendingNumber = phoneNumber
    # if "549766158" in phoneNumber:
    #     phoneNumber = "+972549766158"
    # elif "587766185" in phoneNumber:
    #     phoneNumber = "+972526565732"
    # elif "549766185" in phoneNumber:
    #     phoneNumber = "+972546621037"
    # else:
    #     phoneNumber = "+972523793259"
    modem = GsmModem(PORT, BAUDRATE)
    modem.smsTextMode = False
    modem.connect(PIN)
    modem.deleteMultipleStoredSms()
    numberOfBk = len(db.execute("SELECT * FROM '"+table_name+"'").fetchall())
    for i in range(0,numberOfBk-1):
        BKname = db.execute("SELECT name FROM '"+table_name+"' WHERE id= '" + str(i) + "'").fetchone()
        realName = db.execute("SELECT realName FROM '" + table_name + "' WHERE id= '" + str(i) + "'").fetchone()
        exists = db.execute("SELECT EXISTS(SELECT name FROM '"+str(BKname[0])+"' WHERE phoneNumber = '"+phoneNumber+"')")
        if exists!=None:
            BKid = i
            break
    numOfSeats = str(len(get_seatid_from_phone(phoneNumber,BKid).split(" "))-1)
    messageBK="זוהי הודעה מגבאי בית הכנסת " + str(realName[0]).strip() + ":" + " נשמח אם תוכל להחזיר הודעה עם מספר המושבים הפנויים שמוקצים לך ולמשפחתך בשבת הקרובה." + "\nמקסימום: " + numOfSeats #set message for each place
    messagePL="זוהי הודעה מקצין הרכב של חברת " + str(realName[0]).strip() + ":" + " האם תצטרך חניה לרכבך היום בחניון החברה? (כן\לא)"
    message=""
    if table_name == "parking_lots":
        message = messagePL
    else:
        message = messageBK
    print("sending message to " + phoneNumber)
    modem.sendSms(phoneNumber, message)

#Sends a message to every user in the list
def send_all(table_name):
    print("Entered send_all")
    if "batei" in table_name:
        open("AnsweredPhonesBK.txt", "w").close()
    else:
        open("AnsweredPhonesPL.txt", "w").close()
    # send_msg("+972549766158")
    # send_msg("+972587766185")
    # modem.deleteMultipleStoredSms()
    print ("Entered send_all")
    print("Today should send")
    # phoneNumbers = ["972584966113", "972549766158", "972549766185", "972526503331", "972546621037", "972546424084",
    #                 "972526148035", "972526005818", "972523326171", "972587878335", "972526565745", "972526565732",
    #                 "972527335866", "972546456643","972528751879"]
    phoneNumbers = ["972584966113","972549766158","972549766185"]
    # modem.write("AT+CNMI=0,0,0,0,0")
    modem = GsmModem(PORT, BAUDRATE)
    modem.smsTextMode = False
    modem.connect(PIN)
    modem.deleteMultipleStoredSms()
    for number in phoneNumbers:
        send_msg(table_name,number)
        # time.sleep(1)
    modem.close()
    # modem.write('AT+CNMI=' + modem.AT_CNMI)
    print("done sending messages")
    modem = GsmModem(PORT, BAUDRATE, smsReceivedCallbackFunc=handleSms)
    modem.smsTextMode = False
    modem.connect(PIN)
    #!!!!!!!!!!!!! Need to get phone numbers by bk or pl !!!!!!!!!!
    # send_msg(table_name,"+972584966113")
    # send_msg(table_name,"+972549766185")
    # send_msg(table_name,"+972544966113")
    modem.deleteMultipleStoredSms()
    # phoneNumbers = get_all_numbers(table_name).split(" ")
    # for i in range(0,len(phoneNumbers)):
    #     send_msg(table_name,phoneNumbers[i])

#Sends message a second time to every number who didn't return a normal answer.
def send_all_second_time(table_name):
    print ("Entered send_all_second_time")
    if "batei" in table_name:
        with open("AnsweredPhonesBK.txt") as f:
            lineList = f.read()
    else:
        with open("AnsweredPhonesPL.txt") as f:
            lineList = f.read()
    # phoneNumbers = get_all_numbers("parking_lots").split(" ")
    # phoneNumbers = ["+972549766158","+972549766185","+972584966113"] #!!!!!!!!!!!!! Need to get phone numbers by bk or pl !!!!!!!!!!
    phoneNumbers = ["972584966113", "972549766158", "972549766164", "972526503331", "972546621037", "972546424084",
                    "972526148035", "972526005818", "972523326171", "972587878335", "972526565745", "972526565732",
                    "972527335866", "972546456643","972528751879"]
    # phoneNumbers = ["972584966113", "972549766158", "972549766185", "972587878335", "972546621037", "972526148035",
    #                 "972523326171", "972546456643"]
    print(lineList)
    for i in range(0,len(phoneNumbers)): #Check witch number has already responded and send to the remaining numbers.
        if phoneNumbers[i] in lineList: #str(phoneNumbers[i])
            print("Already sent to " + phoneNumbers[i])
            # None
        else:
            print("should send - " + phoneNumbers[i])
            send_msg(table_name,phoneNumbers[i])
    if "batei" in table_name:
        open("AnsweredPhonesBK.txt", "w").close()
    else:
        open("AnsweredPhonesPL.txt", "w").close()

def schedule_all():
    print("Entered Schedule send")
    #schedule.every().thursday.at("18:00").do(send_all,"batei_kneset")
    #schedule.every().friday.at("08:00").do(send_all_second_time,"batei_kneset")
    schedule.every().thursday.at("10:00").do(update_users,"batei_kneset")
    schedule.every().saturday.at("23:00").do(reset_statuses,"batei_kneset")
    schedule.every().day.at("07:00").do(send_all,"parking_lots")
    schedule.every().day.at("08:00").do(send_all_second_time, "parking_lots")
    schedule.every().day.at("22:00").do(reset_statuses,"parking_lots")
    schedule.every().day.at("23:00").do(update_users,"parking_lots")
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
    # check if received sms content is a number
    phoneNumber = sms.number
    # if "549766158" in phoneNumber:
    #     phoneNumber = "+972549766158"
    # elif "584966113" in phoneNumber:
    #     phoneNumber = "+972526565732"
    # elif "549766185" in phoneNumber:
    #     phoneNumber = "+972546621037"
    # else:
    #     phoneNumber = "+972523793259"
    numberOfBk = len(db.execute("SELECT * FROM batei_kneset").fetchall())
    numberOfPl = len(db.execute("SELECT * FROM parking_lots").fetchall())
    table_name = ""
    msg = str((sms.text).encode('utf-8', 'ignore')).strip(" \t,.\n!")
    if msg == "כן" or msg == "לא":
        for i in range(0, numberOfPl):
            BKname = db.execute("SELECT name FROM parking_lots WHERE id= '" + str(i) + "'").fetchone()
            exists = db.execute(
                "SELECT EXISTS(SELECT name FROM '" + str(BKname[0]) + "' WHERE phoneNumber = '" + phoneNumber + "')")
            if exists != None:
                BKid = i
                table_name = "parking_lots"
                break
        if table_name=="":
            for i in range(0, numberOfBk):
                BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(i) + "'").fetchone()
                exists = db.execute(
                    "SELECT EXISTS(SELECT name FROM '" + str(BKname[0]) + "' WHERE phoneNumber = '" + phoneNumber + "')")
                if exists != None:
                    BKid = i
                    table_name = "batei_kneset"
                    break
    else:
        for i in range(0, numberOfBk):
            BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(i) + "'").fetchone()
            exists = db.execute(
                "SELECT EXISTS(SELECT name FROM '" + str(BKname[0]) + "' WHERE phoneNumber = '" + phoneNumber + "')")
            if exists != None:
                BKid = i
                table_name = "batei_kneset"
                break
        if table_name == "":
            for i in range(0, numberOfPl):
                BKname = db.execute("SELECT name FROM parking_lots WHERE id= '" + str(i) + "'").fetchone()
                exists = db.execute(
                    "SELECT EXISTS(SELECT name FROM '" + str(BKname[0]) + "' WHERE phoneNumber = '" + phoneNumber + "')")
                if exists != None:
                    BKid = i
                    table_name = "parking_lots"
                    break
    if exists == None:
        print("Phone number not found")
        sms.reply(u"מספר הטלפון שלך לא נמצא במערכת שלנו, אנא דבר עם האחראי.")
    print("BKid = " + str(BKid))
    # phoneNumber = "+972523793259"
    sameNum = False
    firstNo = True
    if table_name == "parking_lots":
        if msg == "כן":
            with open('takenSpots.txt') as f:
                datafile = f.readlines()
            for line in datafile:
                if str(sms.number) in line:
                    sms.reply(u"מספר החניה שלך: " + line.split("\t")[2])
                    sameNum = True
                    break
            if sameNum == False:
                nextParking = get_next_parking(BKid, phoneNumber)
                if nextParking == 0:
                    sms.reply(u"נגמרו החניות הפנויות, אתה מתבקש לחנות בחניית האורחים.")
                else:
                    sms.reply(u"מספר החניה שלך: " + nextParking)
                with open("AnsweredPhonesPL.txt", "a") as myFile:
                    myFile.write(sms.number + " ")
        elif msg == "לא":
            with open('takenSpots.txt') as f:
                datafile = f.readlines()
                tmpLines = datafile
            for line in datafile:
                if str(sms.number) in line:
                    sms.reply(u" תודה רבה! החניה שוחררה בהצלחה, ניתן לענות שוב 'כן' על מנת לשוב ולקבל חניה.")
                    firstNo=False
                    with open("takenSpots.txt", "w") as f:
                        for l in tmpLines:
                            if l.strip("\n") != line.strip("\n"):
                                f.write(l)
                    reset_parking_status(BKid, line.split("\t")[2])
                    break
            if firstNo==True:
                sms.reply(u"תודה רבה! ניתן לענות 'כן' על מנת לקבל חניה.")
            with open("AnsweredPhonesPL.txt", "a") as myFile:
                myFile.write(sms.number + " ")
        else:
            sms.reply(u"תגובתך אינה תואמת את המבוקש, אנא נסה שוב.")
    else:
        numOfSeats = str(len(get_seatid_from_phone(table_name, phoneNumber, BKid).split(" ")) - 1)  # sms.number
        # numOfSeats = 2
        print(numOfSeats)
        print("sms.text = " + msg)
        if msg.isdigit() == True:
            if int(msg) <= int(numOfSeats):
                sms.reply(msg + u" מקומות עודכנו בהצלחה!")
                with open("AnsweredPhonesBK.txt", "a") as myFile:
                    myFile.write(sms.number + " ")
                set_statuses_from_phone(table_name, phoneNumber, int(msg), BKid)  # sms.number
                print(get_statuses(table_name, BKid))
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
    finalReturnMessages = ""
    global finalReturnMemory
    finalReturnMemory = ""
    i = 0
    if not values:
        return ('000')
    else:
        for row in values:
            if i <= 11:
                finalReturnMessages = finalReturnMessages + row[0] + "\n"
            else:
                finalReturnMemory = finalReturnMemory + row[0] + "\n"
            i = i + 1
            # Print columns A and E, which correspond to indices 0 and 4.
            # print('%s' % (row[0]))
        return (finalReturnMessages)

def update_users(table_name):

    tableSize = len(db.execute("SELECT id FROM '"+table_name+"'").fetchall())
    if "batei" in table_name:
        letter = chr(tableSize*3+64) #65='A'
        RANGE_NAME_USERS = 'מפת בתי הכנסת!A3:' + letter
    else:
        letter = chr(tableSize*3+64) #65='A'
        RANGE_NAME_USERS = 'רשימת חניונים!A3:' + letter
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
    global parkingLotNames
    parkingLotNames = ""
    if not values:
        return ('000')
    else:
        print("tablesize: " + str(tableSize))
        for i in range(0,tableSize):
            for row in values:
                if len(row)>=(i*3)+1:
                    if len(row)%3==1 and (i+1)*3>len(row): #Checks if the row isn't empty for the given PL.
                        # if row[1+(i*3)].encode('utf-8')=="":
                        #     finalReturn = finalReturn + row[0 + (i * 3)] + "\t" + "_" + "\t" + "_" + "\n"
                        #     parkingLotNames = parkingLotNames + row[0 + (i * 3)] + "\n"
                        # elif row[0+(i*3)].encode('utf-8')=="":
                        #     finalReturn = finalReturn + "_" + "\t" + row[1+(i*3)] + "\t" + row[2+(i*3)] + "\n"
                        # else:
                        #     finalReturn = finalReturn + row[0 + (i * 3)] + "\t" + row[1 + (i * 3)] + "\t" + row[2 + (i * 3)] + "\n"
                        #     parkingLotNames = parkingLotNames + row[0 + (i * 3)] + "\n"
                        finalReturn = finalReturn + row[0 + (i * 3)] + "\t" + "_" + "\t" + "_" + "\n"
                        parkingLotNames = parkingLotNames + row[0 + (i * 3)] + "\n"
                    elif len(row)%3==0 or (len(row)%3==1 and (i+1)*3<len(row)):
                        if row[0 + (i * 3)].encode('utf-8') == "":
                            finalReturn = finalReturn + "_" + "\t" + row[1 + (i * 3)] + "\t" + row[2 + (i * 3)] + "\n"
                        else:
                            finalReturn = finalReturn + row[0 + (i * 3)] + "\t" + row[1 + (i * 3)] + "\t" + row[2 + (i * 3)] + "\n"
                            parkingLotNames = parkingLotNames + row[0 + (i * 3)] + "\n"
                    else:
                        print ("Row not acceptable")
                # print (finalReturn)
                # Print columns A, B and C, which correspond to indices 0, 1 and 2.
                # print('%s, %s, %s' % (row[0], row[1], row[2]))
            finalReturn =finalReturn+"|"
            parkingLotNames = parkingLotNames + "|"
        # print(finalReturn)
        if "batei" in table_name:
            temp_file = open("BkUsersFile.txt").read()
            if temp_file.decode('utf-8') != finalReturn:
                with open('BkUsersFile.txt', 'w') as testFile:
                    testFile.write(finalReturn.encode('utf-8'))
                user_insertion_BK()
        else:
            temp_file = open("PlUsersFile.txt").read()
            if temp_file.decode('utf-8') != finalReturn:
                with open('PlUsersFile.txt', 'w') as testFile:
                    testFile.write(finalReturn.encode('utf-8'))
                user_insertion_PL()
                # print(parkingLotNames)
        # return (finalReturn)

def manage_data(conn,addr):
    while 1:
            print ("recieving...")
            data = conn.recv(25)
            if not data:
                # client closed the connection
                break
            print (data)

            appType = data.split(" ")[0][2:]
            if appType == "0": #!!!!!!!!!!!!!  Batei Kneset App  !!!!!!!!!!!

                table_name = "batei_kneset"
                global finalReturnMemory
                if "get statuses" in data:
                    print ("received get statuses BK command")
                    statuses = get_statuses(table_name, data.split(" ")[3])
                    print(statuses)
                    print(str(len(statuses)))
                    if len(str(len(statuses))) == 1:
                        conn.send("00" + str(len(statuses)))
                    elif len(str(len(statuses))) == 2:
                        conn.send("0" + str(len(statuses)))
                    else:
                        conn.send(str(len(statuses)))
                    data = conn.recv(12)
                    print("data: " + data)
                    if "received" in data:
                        conn.send(statuses) #sends list of seat id's where status = 1
                    break
                elif "get messages" in data:
                    print ("received get messages BK command")
                    messages = update_messages(data.split(" ")[3])
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
                elif "get memory" in data:
                    print("received get memory BK command")
                    update_messages(data.split(" ")[3])
                    messages = finalReturnMemory
                    print(len(messages))
                    if len(str(len(messages) * 2)) == 1:
                        conn.send("00" + str(len(messages) * 2))
                    elif len(str(len(messages) * 2)) == 2:
                        conn.send("0" + str(len(messages) * 2))
                    else:
                        conn.send(str(len(messages) * 2))
                    # conn.send(str(len(messages)*2))
                    data = conn.recv(12)
                    print("data: " + data)
                    if "received" in data:
                        messages = messages.encode('utf-8')
                        messages = messages + messages
                        print(messages)
                        conn.send(messages)  # sends list of messages
                    else:
                        conn.send("000")
                    break
                elif "get updates" in data:
                    print ("received get updates BK command")
                    allUsers = open("BkUsersFile.txt").read()
                    splitUsers = allUsers.split("|")
                    users=splitUsers[int(data.split(" ")[3])]
                    users = users[:-1]
                    userLines = users.splitlines()
                    print(len(userLines))
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
                            print("i = " + str(i))
                            print ("--- Updating Users ---")
                            conn.send(messages) #sends list of users
                            conn.recv(12)
                            i=i+20
                    joinedLines='\n'.join(userLines[i:])+'\n'
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
                        print("i = " + str(i))
                        print ("--- Updating Users ---")
                        conn.send(messages) #sends list of users
                        conn.recv(12)
                    conn.send('0')
                    conn.recv(12)
                elif "get password" in data:
                    print ("received get password BK command")
                    query = "SELECT password FROM batei_kneset WHERE id = '" + data.split(" ")[3] + "'"
                    password = db.execute(query)
                    password = password.fetchone()
                    conn.send(str(password[0]))
                else:
                    print ("error: Unknown command BK")
                    conn.send("Unknown command")
                    time.sleep(1)

            elif appType == "1": #!!!!!!!!!!!!!  Parking Lots App  !!!!!!!!!!!

                table_name = "parking_lots"
                global parkingLotNames
                if "get statuses" in data:
                    print ("received get statuses PL command")
                    statuses = get_statuses(table_name, data.split(" ")[3])
                    print(statuses)
                    print(str(len(statuses)))
                    if len(str(len(statuses))) == 1:
                        conn.send("00" + str(len(statuses)))
                    elif len(str(len(statuses))) == 2:
                        conn.send("0" + str(len(statuses)))
                    else:
                        conn.send(str(len(statuses)))
                    data = conn.recv(12)
                    print("data: " + data)
                    if "received" in data:
                        print("sending statuses")
                        conn.send(statuses) #sends list of seat id's where status = 1
                    break
                elif "get user" in data:
                    print("received get user PL command")
                    BKid = data.split(" ")[4]
                    BKname = db.execute("SELECT name FROM parking_lots WHERE id= '" + BKid + "'").fetchone()
                    parkingId = data.split(" ")[3]
                    send=""
                    allLines = open("takenSpots.txt").readlines()
                    for i in range(0,len(allLines)):
                        splitedLine = allLines[i].split("\t")
                        if splitedLine[0].rstrip() == BKid and splitedLine[2] == get_parking_name_from_id(BKname,parkingId):
                            send=splitedLine[1].strip()
                            if len(str(len(send))) == 1:
                                conn.send("0" + str(len(send)))
                            else:
                                conn.send(str(len(send)))
                            conn.recv(12)
                            conn.send(send)
                            conn.recv(12)
                            break
                    if send=="":
                        conn.send("00")
                elif "get updates" in data:
                    print ("received get updates PL command")
                    # update_users(table_name)
                    allParkings = open("parkingUsers.txt").read()
                    # splitUsers = parkingLotNames.split("|")
                    splitUsers = allParkings.split("|")
                    users=splitUsers[int(data.split(" ")[3])]
                    users = users[:-1]
                    userLines = users.splitlines()
                    i=0
                    joinedLines=''
                    while (i+50)<len(userLines):
                        joinedLines='\n'.join(userLines[i:i+50])+'\n'
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
                            i=i+50
                    joinedLines='\n'.join(userLines[i:])+'\n'
                    print(str(len(str(len(joinedLines)))))
                    conn.send(str(len(str(len(joinedLines) * 2 + 1))))
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
                elif "get lists" in data:
                    print("received get lists command")
                    PLid = data.split(" ")[4]
                    listType = int(data.split(" ")[3])
                    fileLines = ""

                    fileName = "takenSpots.txt"
                    with open(fileName, 'r') as currFile:
                        fileLinesDay = currFile.readlines()
                        tmpLines = ""
                        for line in fileLinesDay:
                            if line.split("\t")[0].rstrip() == PLid:
                                tmpLines += " ".join(line.split("\t")[1:]) + "\n"
                        fileLinesDay = tmpLines.split("\n")

                    if listType == 0:  # Month.
                        fileName = "monthly_update_" + PLid + ".txt"
                        with open(fileName, 'r') as currFile:
                            fileLines = currFile.readlines()
                            fileLines.append(str("\n" + datetime.today().strftime('%Y-%m-%d') + "\n"))
                            fileLines+= fileLinesDay # Send also current day.
                    elif listType == 1:  # Week.
                        fileName = "weekly_update_" + PLid + ".txt"
                        with open(fileName, 'r') as currFile:
                            fileLines = currFile.readlines()
                            fileLines.append(str("\n" + datetime.today().strftime('%Y-%m-%d') + "\n"))
                            fileLines += fileLinesDay  # Send also current day.
                    elif listType == 2:  # Day.
                        fileLines=("\n" + datetime.today().strftime('%Y-%m-%d') + "\n").split("\n")
                        fileLines += fileLinesDay  # Send also current day.

                    i = 0
                    joinedLines = ''
                    while (i + 50) < len(fileLines):
                        joinedLines = '\n'.join(fileLines[i:i + 50]) + '\n'
                        print(str(len(str(len(joinedLines)))))
                        conn.send(str(len(str(len(joinedLines) * 2 + 1))))
                        data = conn.recv(12)
                        print(str(len(joinedLines)))
                        conn.send(str(len(joinedLines) * 2 + 1))
                        data = conn.recv(12)
                        print("data: " + data)
                        if "received" in data:
                            messages = joinedLines
                            messages = messages + "|" + messages
                            print("--- Sent List ---")
                            conn.send(messages)  # sends list of statuses
                            conn.recv(12)
                            print("i = " + str(i))
                            i = i + 50
                    joinedLines = '\n'.join(fileLines[i:]) + '\n'
                    print(str(len(str(len(joinedLines)))))
                    conn.send(str(len(str(len(joinedLines) * 2 + 1))))
                    data = conn.recv(12)
                    print(str(len(joinedLines)))
                    conn.send(str(len(joinedLines) * 2 + 1))
                    data = conn.recv(12)
                    print("data: " + data)
                    if "received" in data:
                        messages = joinedLines
                        messages = messages + "|" + messages
                        print("--- Sent List ---")
                        conn.send(messages)  # sends list of statuses
                        conn.recv(12)
                    conn.send('0')
                    conn.recv(12)
                elif "get password" in data:
                    print ("received get password PL command")
                    query = "SELECT password FROM parking_lots WHERE id = '" + data.split(" ")[3] + "'"
                    password = db.execute(query)
                    password = password.fetchone()
                    conn.send(str(password[0]))
                else:
                    print ("error: Unknown command PL")
                    conn.send("Unknown command")
                    time.sleep(1)
    db.commit()

parkingLotNames = ""
finalReturnMemor=""
if __name__ == '__main__':
    db = sqlite3.connect('parkingUsers.db', check_same_thread=False)
    db.text_factory = str
    c = db.cursor()
    # update_users()
    reset_statuses("parking_lots")
    reset_statuses("batei_kneset")
    #update_users("parking_lots")
    # user_insertion()
    threading.Thread(target = schedule_all).start()

    modem = GsmModem(PORT, BAUDRATE, smsReceivedCallbackFunc=handleSms)
    modem.smsTextMode = False
    modem.connect(PIN)
    modem.deleteMultipleStoredSms()
    send_all("parking_lots")
    #send_msg("parking_lots","+972527335866")#"972527335866", "972546456643"
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
    atexit.register(db.close)# -*- coding: utf-8 -*-
# from __future__ import print_function
# import atexit
# import socket
# import sqlite3
# import threading
# from datetime import datetime, date, time, timedelta
# import time
# import schedule
# import pickle
# import os.path
# import sys
# from googleapiclient.discovery import build
# from google_auth_oauthlib.flow import InstalledAppFlow
# from google.auth.transport.requests import Request
# from gsmmodem.modem import GsmModem
# import unicodedata
#
# # If modifying these scopes, delete the file token.pickle.
# SCOPES = ['https://www.googleapis.com/auth/spreadsheets.readonly']
#
# # The ID and range of a sample spreadsheet.
# SPREADSHEET_ID = '1D_E3p_GRtuh08MsN0fo2lXFR-94YIvQN1MKg6MyQVUk'
# RANGE_NAME = 'Sheet1!A1:A'
# RANGE_NAME_USERS = 'Sheet2!A2:C'
#
# PORT = 'COM34'
# BAUDRATE = 115200
# PIN = None # SIM card PIN (if any)
#
# HOSTCLIENT = '0.0.0.0'
# PORTCLIENT = 443
#
# #inserts employees from given file of employees into the table "employees"
# def user_insertion_BK():
#     emp_file = open("BkUsersFile.txt").read()
#     splited = emp_file.split("|")
#     numberOfBk = len(db.execute("SELECT * FROM batei_kneset").fetchall())
#     for j in range(0,numberOfBk):
#         a=splited[j].splitlines()
#         # for i in range(0,len(a)-1):
#         #     a[i] = a[i].split("\t")
#         BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(j) + "'").fetchone()
#         print(BKname[0])
#         query = "DELETE FROM '"+ str(BKname[0]) +"' "
#         db.execute(query)
#         query = "INSERT INTO '"+str(BKname[0])+"' VALUES (?, ?, ? , ?)"
#         status = 0
#         # while(a[i] != None):
#         print (a[208])
#         print(len(a))
#         for i in range(0,len(a)):
#             a[i] = a[i].split("\t")
#             # print(a[i][1].decode("iso-8859-8"))
#             # name = a[i][1].decode("iso-8859-8")
#             name = a[i][1]
#             if "פנוי" in name:
#                 phoneNumber = "0000000000"
#             else:
#                 phoneNumber = "+" + a[i][2]
#             id = int(a[i][0])
#             db.execute(query, (id, name, phoneNumber, status))
#             # print i
#             # i+=1
#         db.commit()
#         print ("Added employees successfully BK - " + str(j))
#
# def user_insertion_PL():
#     emp_file = open("PlUsersFile.txt").read()
#     splited = emp_file.split("|")
#     numberOfBk = len(db.execute("SELECT * FROM parking_lots").fetchall())
#     for j in range(0,numberOfBk):
#         a=splited[j].splitlines()
#         # for i in range(0,len(a)-1):
#         #     a[i] = a[i].split("\t")
#         PLname = db.execute("SELECT name FROM parking_lots WHERE id= '" + str(j) + "'").fetchone()
#         print(PLname[0])
#         query = "DELETE FROM '"+ str(PLname[0]) +"' "
#         db.execute(query)
#         query = "INSERT INTO '"+str(PLname[0])+"' VALUES (?, ?, ?, ?, ?)"
#         status = 0
#         # while(a[i] != None):
#         print(len(a))
#         for i in range(0,len(a)):
#             a[i] = a[i].split("\t")
#             id = i + 1
#             # print ("a[0] - " + a[0])
#             # print("a[1] - " + a[1])
#             if "_" in a[i][0]: #No parking in line
#                 print ("No Parking in line")
#                 name = a[i][1]
#                 phoneNumber = "+" + a[i][2]
#                 db.execute(query, (id, name, phoneNumber, None, None))
#             elif "_" in a[i][1]: #No user in line
#                 parkingName = a[i][0]
#                 db.execute(query, (id, None, None, status, parkingName))
#             else: #Both in line
#                 name = a[i][1]
#                 phoneNumber = "+" + a[i][2]
#                 parkingName = a[i][0]
#                 db.execute(query, (id, name, phoneNumber, status, parkingName))
#                 # print i
#                 # i+=1
#         db.commit()
#         print ("Added employees successfully PL - " + str(j))
#
# def get_statuses(table_name, BKid):
#     print ("Getting statuses")
#     BKname = db.execute("SELECT name FROM '"+table_name+"' WHERE id= '" + str(BKid) + "'").fetchone()
#     query = "SELECT id FROM '" + str(BKname[0]) + "' WHERE status = 1"
#     all = db.execute(query)
#     allEmps = all.fetchall()
#     finalReturn=""
#     if allEmps != None:
#         for i in range(0,len(allEmps)):
#             send = str(allEmps[i][0])
#             finalReturn = finalReturn+send+" "
#         return (finalReturn)
#         #need to send to client
#
# def reset_statuses(table_name):
#     numberOfBk = len(db.execute("SELECT * FROM '"+table_name+"'").fetchall())
#     for i in range(0,numberOfBk):
#         BKname = db.execute("SELECT name FROM '"+table_name+"' WHERE id= '" + str(i) + "'").fetchone()
#         print(BKname[0])
#         query = "UPDATE '"+str(BKname[0])+"' SET status = 0"
#         c.execute(query)
#     db.commit()
#     if table_name == "parking_lots":
#         if int(datetime.today().day) == 1:
#             for k in range(0, numberOfBk):  # Delete all files for new month
#                 fileName = "monthly_update_" + str(k) + ".txt"
#                 with open(fileName, 'w') as currFile:
#                     currFile.write("")
#         if int(datetime.today().weekday()) == 6:
#             print("deleting week")
#             for k in range(0, numberOfBk):  # Delete all files for new week
#                 fileName = "weekly_update_" + str(k) + ".txt"
#                 with open(fileName, 'w') as currFile:
#                     currFile.write("")
#
#         for j in range(0, numberOfBk): # Add todays date to all files
#             fileNameM = "monthly_update_" + str(j) + ".txt"
#             fileNameW = "weekly_update_" + str(j) + ".txt"
#             with open(fileNameM, 'a+') as currFile:
#                 currFile.write("\n" + datetime.today().strftime('%Y-%m-%d') + "\n")
#             with open(fileNameW, 'a+') as currFile:
#                 currFile.write("\n" + datetime.today().strftime('%Y-%m-%d') + "\n")
#
#         allLines = open("takenSpots.txt").readlines()
#         for i in range(0, len(allLines)): # Put each line in the correct file
#             splitedLine = allLines[i].split("\t")
#             PLid = splitedLine[0].rstrip()
#             fileNameM = "monthly_update_" + PLid + ".txt"
#             fileNameW = "weekly_update_" + PLid + ".txt"
#             file = open(fileNameM, 'a+')
#             file.write("")
#             with open(fileNameM, 'a+') as currFile:
#                 currFile.write(" ".join(allLines[i].split("\t")[1:]))
#             file = open(fileNameW, 'a+')
#             file.write("")
#             with open(fileNameW, 'a+') as currFile:
#                 currFile.write(" ".join(allLines[i].split("\t")[1:]))
#
#         with open("takenSpots.txt", 'w') as dayFile: # Delete day file
#             dayFile.write("")
#
#
#
# def get_users(table_name, BKid):
#     BKname = db.execute("SELECT name FROM '"+table_name+"' WHERE id= '" + str(BKid) + "'").fetchone()
#     query = "SELECT * FROM '"+str(BKname[0])+"'"
#     all = db.execute(query)
#     allEmps = all.fetchall()
#     finalReturn=""
#     if allEmps != None:
#         for i in range(0,len(allEmps)):
#             send = str(allEmps[i][0]) + " " + allEmps[i][1] + " " + allEmps[i][2] + " " + str(allEmps[i][3])
#             finalReturn = finalReturn+send+","
#         print(finalReturn)
#         return finalReturn
#
# def get_user_from_phone(table_name, phoneNumber, BKid):
#     BKname = db.execute("SELECT name FROM '"+table_name+"' WHERE id= '" + str(BKid) + "'").fetchone()
#     query = "SELECT * FROM '"+str(BKname[0])+"' WHERE phoneNumber = '" + phoneNumber + "'"
#     user = db.execute(query)
#     user = user.fetchall()
#     finalReturn=""
#     if user != None:
#         for i in range(0,len(user)):
#             send = str(user[i][0]) + " " + user[i][1] + " " + user[i][2] + " " + str(user[i][3])
#             finalReturn = finalReturn+send+","
#         return finalReturn
#
# def get_seatid_from_phone(phoneNumber, BKid):
#     BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(BKid) + "'").fetchone()
#     query = "SELECT id FROM '"+str(BKname[0])+"' WHERE phoneNumber = '" + phoneNumber + "'"
#     user = db.execute(query)
#     user = user.fetchall()
#     finalReturn=""
#     if user != None:
#         for i in range(0,len(user)):
#             send = str(user[i][0])
#             finalReturn = finalReturn+send+" "
#         return(finalReturn)
#
# def get_all_numbers(table_name):
#     numberOfBk = len(db.execute("SELECT * FROM '"+table_name+"'").fetchall())
#     for i in range(0,numberOfBk-1):
#         BKname = db.execute("SELECT name FROM '"+table_name+"' WHERE id= '" + str(i) + "'").fetchone()
#         query = "SELECT phoneNumber FROM '"+str(BKname[0])+"'"
#         phoneNumbers = db.execute(query)
#         phoneNumbers = phoneNumbers.fetchall()
#         finalReturn=""
#         if phoneNumbers != None:
#             for i in range(0,len(phoneNumbers)):
#                 if "+972" in phoneNumbers[i][0]:
#                     send = str(phoneNumbers[i][0])
#                     finalReturn = finalReturn+send+" "
#             finalfinalReturn = finalfinalReturn+finalReturn+" "
#     return (finalfinalReturn)
#
# def get_parking_name_from_id(BKname,id):
#     query = "SELECT parkingName FROM '" + str(BKname[0]) + "' WHERE id = '" + str(id) + "'"
#     name = db.execute(query)
#     name = name.fetchone()[0]
#     return(name)
#
# def get_id_from_parking_name(BKname,PLname):
#     query = "SELECT id FROM '" + str(BKname[0]) + "' WHERE parkingName = '" + PLname + "'"
#     name = db.execute(query)
#     name = name.fetchone()[0]
#     return(name)
#
# def get_name_from_phone(table_name, phoneNumber, BKid):
#     BKname = db.execute("SELECT name FROM '"+table_name+"' WHERE id= '" + str(BKid) + "'").fetchone()
#     query = "SELECT name FROM '"+str(BKname[0])+"' WHERE phoneNumber = '" + phoneNumber + "'"
#     user = db.execute(query)
#     user = user.fetchone()[0]
#     return user
#
# def get_next_parking(BKid,phoneNumber):
#     BKname = db.execute("SELECT name FROM parking_lots WHERE id= '" + str(BKid) + "'").fetchone()
#     query = "SELECT id FROM '"+str(BKname[0])+"' WHERE status = 0"
#     id = db.execute(query)
#     id = id.fetchone()
#     if id:
#         id=id[0]
#         name = get_name_from_phone("parking_lots", phoneNumber, BKid)
#         with open("takenSpots.txt", "a+") as myFile:
#             myFile.write(str(BKid) + "\t" + name + "\t" + get_parking_name_from_id(BKname,id) + "\t" + str(phoneNumber) +"\n")
#         query = "UPDATE '"+str(BKname[0])+"' SET status = 1 WHERE id = '" + str(id) + "'"
#         db.execute(query)
#         db.commit()
#         return str(get_parking_name_from_id(BKname, id))
#     else:
#         return 0
#
# def reset_parking_status(BKid,parkingId):
#     BKname = db.execute("SELECT name FROM parking_lots WHERE id= '" + str(BKid) + "'").fetchone()
#     query = "UPDATE '"+str(BKname[0])+"' SET status = 0 WHERE parkingName = '" + parkingId + "'"
#     db.execute(query)
#     db.commit()
#
#
# def send_msg(table_name, phoneNumber):
#     # sendingNumber = phoneNumber
#     # if "549766158" in phoneNumber:
#     #     phoneNumber = "+972549766158"
#     # elif "587766185" in phoneNumber:
#     #     phoneNumber = "+972526565732"
#     # elif "549766185" in phoneNumber:
#     #     phoneNumber = "+972546621037"
#     # else:
#     #     phoneNumber = "+972523793259"
#     numberOfBk = len(db.execute("SELECT * FROM '"+table_name+"'").fetchall())
#     for i in range(0,numberOfBk-1):
#         BKname = db.execute("SELECT name FROM '"+table_name+"' WHERE id= '" + str(i) + "'").fetchone()
#         realName = db.execute("SELECT realName FROM '" + table_name + "' WHERE id= '" + str(i) + "'").fetchone()
#         exists = db.execute("SELECT EXISTS(SELECT name FROM '"+str(BKname[0])+"' WHERE phoneNumber = '"+phoneNumber+"')")
#         if exists!=None:
#             BKid = i
#             break
#     numOfSeats = str(len(get_seatid_from_phone(phoneNumber,BKid).split(" "))-1)
#     messageBK="זוהי הודעה מגבאי בית הכנסת " + str(realName[0]).strip() + ":" + " נשמח אם תוכל להחזיר הודעה עם מספר המושבים הפנויים שמוקצים לך ולמשפחתך בשבת הקרובה." + "\nמקסימום: " + numOfSeats #set message for each place
#     messagePL="זוהי הודעה מקצין הרכב של חברת " + str(realName[0]).strip() + ":" + " האם תצטרך חניה לרכבך היום בחניון החברה? (כן\לא)"
#     message=""
#     if table_name == "parking_lots":
#         message = messagePL
#     else:
#         message = messageBK
#     print("sending message to " + phoneNumber)
#     modem.sendSms(phoneNumber, message)
#
# #Sends a message to every user in the list
# def send_all(table_name):
#     print("Entered send_all")
#     if "batei" in table_name:
#         open("AnsweredPhonesBK.txt", "w").close()
#     else:
#         open("AnsweredPhonesPL.txt", "w").close()
#     # send_msg("+972549766158")
#     # send_msg("+972587766185")
#     # modem.deleteMultipleStoredSms()
#     print ("Entered send_all")
#     print("Today should send")
#     # phoneNumbers = ["972584966113", "972549766158", "972549766185", "972526503331", "972546621037", "972546424084",
#     #                 "972526148035", "972526005818", "972523326171", "972587878335", "972526565745", "972526565732",
#     #                 "972527335866", "972546456643","972528751879"]
#     phoneNumbers = ["972584966113","972549766158","972549766185"]
#     # modem.write("AT+CNMI=0,0,0,0,0")
#     for number in phoneNumbers:
#         send_msg(table_name,number)
#         # time.sleep(1)
#     # modem.write('AT+CNMI=' + modem.AT_CNMI)
#     print("done sending messages")
#     #!!!!!!!!!!!!! Need to get phone numbers by bk or pl !!!!!!!!!!
#     # send_msg(table_name,"+972584966113")
#     # send_msg(table_name,"+972549766185")
#     # send_msg(table_name,"+972544966113")
#     modem.deleteMultipleStoredSms()
#     # phoneNumbers = get_all_numbers(table_name).split(" ")
#     # for i in range(0,len(phoneNumbers)):
#     #     send_msg(table_name,phoneNumbers[i])
#
# #Sends message a second time to every number who didn't return a normal answer.
# def send_all_second_time(table_name):
#     print ("Entered send_all_second_time")
#     if "batei" in table_name:
#         with open("AnsweredPhonesBK.txt") as f:
#             lineList = f.read()
#     else:
#         with open("AnsweredPhonesPL.txt") as f:
#             lineList = f.read()
#     # phoneNumbers = get_all_numbers("parking_lots").split(" ")
#     # phoneNumbers = ["+972549766158","+972549766185","+972584966113"] #!!!!!!!!!!!!! Need to get phone numbers by bk or pl !!!!!!!!!!
#     #phoneNumbers = ["972584966113", "972549766158", "972549766164", "972526503331", "972546621037", "972546424084",
#     #                "972526148035", "972526005818", "972523326171", "972587878335", "972526565745", "972526565732",
#     #                "972527335866", "972546456643","972528751879"]
#     phoneNumbers = ["972584966113", "972549766158", "972549766185"]
#     print(lineList)
#     for i in range(0,len(phoneNumbers)): #Check witch number has already responded and send to the remaining numbers.
#         if phoneNumbers[i] in lineList: #str(phoneNumbers[i])
#             print("Already sent to " + phoneNumbers[i])
#             # None
#         else:
#             print("should send - " + phoneNumbers[i])
#             send_msg(table_name,phoneNumbers[i])
#     if "batei" in table_name:
#         open("AnsweredPhonesBK.txt", "w").close()
#     else:
#         open("AnsweredPhonesPL.txt", "w").close()
#
# def schedule_all():
#     print("Entered Schedule send")
#     #schedule.every().thursday.at("18:00").do(send_all,"batei_kneset")
#     #schedule.every().friday.at("08:00").do(send_all_second_time,"batei_kneset")
#     schedule.every().thursday.at("10:00").do(update_users,"batei_kneset")
#     schedule.every().saturday.at("23:00").do(reset_statuses,"batei_kneset")
#     schedule.every().day.at("07:00").do(send_all,"parking_lots")
#     schedule.every().day.at("08:00").do(send_all_second_time, "parking_lots")
#     schedule.every().day.at("22:00").do(reset_statuses,"parking_lots")
#     schedule.every().day.at("23:00").do(update_users,"parking_lots")
#     # schedule.every().thursday.at("21:25").do(send_all)
#     # schedule.every().thursday.at("21:29").do(send_all_second_time)
#     # schedule.every().thursday.at("14:03").do(reset_statuses)
#     while True:
#         schedule.run_pending()
#         time.sleep(60)
#
# def set_statuses_from_phone(phoneNumber,number, BKid):
#     BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(BKid) + "'").fetchone()
#     seats = get_seatid_from_phone(phoneNumber, BKid).split(" ")
#     for i in range(0,len(seats)):
#         if i< number:
#             query = "UPDATE '"+str(BKname[0])+"' SET status = 1 WHERE id = '" + seats[i] + "'"
#             db.execute(query)
#         else:
#             query = "UPDATE '"+str(BKname[0])+"' SET status = 0 WHERE id = '" + seats[i] + "'"
#             db.execute(query)
#     print("set")
#     db.commit()
#
# def handleSms(sms):
#     print(u'== SMS message received ==\nFrom: {0}\nTime: {1}\nMessage:\n{2}\n'.format(sms.number, sms.time, sms.text))
#     # check if received sms content is a number
#     phoneNumber = sms.number
#     # if "549766158" in phoneNumber:
#     #     phoneNumber = "+972549766158"
#     # elif "584966113" in phoneNumber:
#     #     phoneNumber = "+972526565732"
#     # elif "549766185" in phoneNumber:
#     #     phoneNumber = "+972546621037"
#     # else:
#     #     phoneNumber = "+972523793259"
#     numberOfBk = len(db.execute("SELECT * FROM batei_kneset").fetchall())
#     numberOfPl = len(db.execute("SELECT * FROM parking_lots").fetchall())
#     table_name = ""
#     msg = str((sms.text).encode('utf-8', 'ignore')).strip(" \t,.\n!")
#     if msg == "כן" or msg == "לא":
#         for i in range(0, numberOfPl):
#             BKname = db.execute("SELECT name FROM parking_lots WHERE id= '" + str(i) + "'").fetchone()
#             exists = db.execute(
#                 "SELECT EXISTS(SELECT name FROM '" + str(BKname[0]) + "' WHERE phoneNumber = '" + phoneNumber + "')")
#             if exists != None:
#                 BKid = i
#                 table_name = "parking_lots"
#                 break
#         if table_name=="":
#             for i in range(0, numberOfBk):
#                 BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(i) + "'").fetchone()
#                 exists = db.execute(
#                     "SELECT EXISTS(SELECT name FROM '" + str(BKname[0]) + "' WHERE phoneNumber = '" + phoneNumber + "')")
#                 if exists != None:
#                     BKid = i
#                     table_name = "batei_kneset"
#                     break
#     else:
#         for i in range(0, numberOfBk):
#             BKname = db.execute("SELECT name FROM batei_kneset WHERE id= '" + str(i) + "'").fetchone()
#             exists = db.execute(
#                 "SELECT EXISTS(SELECT name FROM '" + str(BKname[0]) + "' WHERE phoneNumber = '" + phoneNumber + "')")
#             if exists != None:
#                 BKid = i
#                 table_name = "batei_kneset"
#                 break
#         if table_name == "":
#             for i in range(0, numberOfPl):
#                 BKname = db.execute("SELECT name FROM parking_lots WHERE id= '" + str(i) + "'").fetchone()
#                 exists = db.execute(
#                     "SELECT EXISTS(SELECT name FROM '" + str(BKname[0]) + "' WHERE phoneNumber = '" + phoneNumber + "')")
#                 if exists != None:
#                     BKid = i
#                     table_name = "parking_lots"
#                     break
#     if exists == None:
#         print("Phone number not found")
#         sms.reply(u"מספר הטלפון שלך לא נמצא במערכת שלנו, אנא דבר עם האחראי.")
#     print("BKid = " + str(BKid))
#     # phoneNumber = "+972523793259"
#     sameNum = False
#     firstNo = True
#     if table_name == "parking_lots":
#         if msg == "כן":
#             with open('takenSpots.txt') as f:
#                 datafile = f.readlines()
#             for line in datafile:
#                 if str(sms.number) in line:
#                     sms.reply(u"מספר החניה שלך: " + line.split("\t")[2])
#                     sameNum = True
#                     break
#             if sameNum == False:
#                 nextParking = get_next_parking(BKid, phoneNumber)
#                 if nextParking == 0:
#                     sms.reply(u"נגמרו החניות הפנויות, אתה מתבקש לחנות בחניית האורחים.")
#                 else:
#                     sms.reply(u"מספר החניה שלך: " + nextParking)
#                 with open("AnsweredPhonesPL.txt", "a") as myFile:
#                     myFile.write(sms.number + " ")
#         elif msg == "לא":
#             with open('takenSpots.txt') as f:
#                 datafile = f.readlines()
#                 tmpLines = datafile
#             for line in datafile:
#                 if str(sms.number) in line:
#                     sms.reply(u" תודה רבה! החניה שוחררה בהצלחה, ניתן לענות שוב 'כן' על מנת לשוב ולקבל חניה.")
#                     firstNo=False
#                     with open("takenSpots.txt", "w") as f:
#                         for l in tmpLines:
#                             if l.strip("\n") != line.strip("\n"):
#                                 f.write(l)
#                     reset_parking_status(BKid, line.split("\t")[2])
#                     break
#             if firstNo==True:
#                 sms.reply(u"תודה רבה! ניתן לענות 'כן' על מנת לקבל חניה.")
#             with open("AnsweredPhonesPL.txt", "a") as myFile:
#                 myFile.write(sms.number + " ")
#         else:
#             sms.reply(u"תגובתך אינה תואמת את המבוקש, אנא נסה שוב.")
#     else:
#         numOfSeats = str(len(get_seatid_from_phone(table_name, phoneNumber, BKid).split(" ")) - 1)  # sms.number
#         # numOfSeats = 2
#         print(numOfSeats)
#         print("sms.text = " + msg)
#         if msg.isdigit() == True:
#             if int(msg) <= int(numOfSeats):
#                 sms.reply(msg + u" מקומות עודכנו בהצלחה!")
#                 with open("AnsweredPhonesBK.txt", "a") as myFile:
#                     myFile.write(sms.number + " ")
#                 set_statuses_from_phone(table_name, phoneNumber, int(msg), BKid)  # sms.number
#                 print(get_statuses(table_name, BKid))
#                 print("done")
#             else:
#                 sms.reply(u"תגובתך אינה תואמת את המבוקש, אנא נסה שוב.")
#         else:
#             sms.reply(u"תגובתך אינה תואמת את המבוקש, אנא נסה שוב.")
#     modem.deleteMultipleStoredSms()
#
# def main():
#     # sendsms("+972549766158", "Hello")
#     print('Initializing modem...')
#     # Uncomment the following line to see what the modem is doing:
#     # logging.basicConfig(format='%(levelname)s: %(message)s', level=logging.DEBUG)
#     # modem = GsmModem(PORT)
#     # modem.connect()
#     # modem.smsc = "+972586279099"
#     # modem.sendSms("+972549766158", "before")
#     # modem.close()
#     numOfSeats = 4
#     message="זוהי הודעה מגבאי בית הכנסת : נשמח אם תוכל להחזיר הודעה עם מספר המושבים הפנויים שמוקצים לך ולמשפחתך בשבת הקרובה." + "\nמקסימום: " + str(numOfSeats)
#     modem = GsmModem(PORT, BAUDRATE, smsReceivedCallbackFunc=handleSms)
#     modem.smsTextMode = False
#     modem.connect(PIN)
#     # modem.smsc = "+972586279099" #golan telecom
#     modem.smsc = "+972521100059" #cellcom
#     # modem.sendSms("+972549766158", "שלום מדבר אמיתי מלכה תוכל להגיב לי בבקשה?")
#     modem.sendSms("+972587766185", message)
#     # modem.smsc = "+972586279099"
#     print('Waiting for SMS message...')
#     try:
#         modem.rxThread.join(2**31) # Specify a (huge) timeout so that it essentially blocks indefinitely, but still receives CTRL+C interrupt signal
#     finally:
#         modem.close()
#
# def update_messages(BKid):
#     """Shows basic usage of the Sheets API.
#     Prints values from a sample spreadsheet.
#     """
#     creds = None
#     letter = chr(int(BKid)*2+65) #65='A'
#     print(letter)
#     RANGE_NAME = 'הודעות גבאים!' + letter + "2:" + letter
#     # The file token.pickle stores the user's access and refresh tokens, and is
#     # created automatically when the authorization flow completes for the first
#     # time.
#     if os.path.exists('token.pickle'):
#         with open('token.pickle', 'rb') as token:
#             creds = pickle.load(token)
#     # If there are no (valid) credentials available, let the user log in.
#     if not creds or not creds.valid:
#         if creds and creds.expired and creds.refresh_token:
#             creds.refresh(Request())
#         else:
#             flow = InstalledAppFlow.from_client_secrets_file(
#                 'credentials.json', SCOPES)
#             creds = flow.run_local_server()
#         # Save the credentials for the next run
#         with open('token.pickle', 'wb') as token:
#             pickle.dump(creds, token)
#
#     service = build('sheets', 'v4', credentials=creds)
#
#     # Call the Sheets API
#     sheet = service.spreadsheets()
#     result = sheet.values().get(spreadsheetId=SPREADSHEET_ID,
#                                 range=RANGE_NAME).execute()
#     values = result.get('values', [])
#     finalReturnMessages = ""
#     global finalReturnMemory
#     finalReturnMemory = ""
#     i = 0
#     if not values:
#         return ('000')
#     else:
#         for row in values:
#             if i <= 11:
#                 finalReturnMessages = finalReturnMessages + row[0] + "\n"
#             else:
#                 finalReturnMemory = finalReturnMemory + row[0] + "\n"
#             i = i + 1
#             # Print columns A and E, which correspond to indices 0 and 4.
#             # print('%s' % (row[0]))
#         return (finalReturnMessages)
#
# def update_users(table_name):
#
#     tableSize = len(db.execute("SELECT id FROM '"+table_name+"'").fetchall())
#     if "batei" in table_name:
#         letter = chr(tableSize*3+64) #65='A'
#         RANGE_NAME_USERS = 'מפת בתי הכנסת!A3:' + letter
#     else:
#         letter = chr(tableSize*3+64) #65='A'
#         RANGE_NAME_USERS = 'רשימת חניונים!A3:' + letter
#     creds = None
#     # The file token.pickle stores the user's access and refresh tokens, and is
#     # created automatically when the authorization flow completes for the first
#     # time.
#     if os.path.exists('token.pickle'):
#         with open('token.pickle', 'rb') as token:
#             creds = pickle.load(token)
#     # If there are no (valid) credentials available, let the user log in.
#     if not creds or not creds.valid:
#         if creds and creds.expired and creds.refresh_token:
#             creds.refresh(Request())
#         else:
#             flow = InstalledAppFlow.from_client_secrets_file(
#                 'credentials.json', SCOPES)
#             creds = flow.run_local_server()
#         # Save the credentials for the next run
#         with open('token.pickle', 'wb') as token:
#             pickle.dump(creds, token)
#
#     service = build('sheets', 'v4', credentials=creds)
#
#     # Call the Sheets API
#     sheet = service.spreadsheets()
#     result = sheet.values().get(spreadsheetId=SPREADSHEET_ID,
#                                 range=RANGE_NAME_USERS).execute()
#     values = result.get('values', [])
#     finalReturn=""
#     global parkingLotNames
#     parkingLotNames = ""
#     if not values:
#         return ('000')
#     else:
#         print("tablesize: " + str(tableSize))
#         for i in range(0,tableSize):
#             for row in values:
#                 if len(row)>=(i*3)+1:
#                     if len(row)%3==1 and (i+1)*3>len(row): #Checks if the row isn't empty for the given PL.
#                         # if row[1+(i*3)].encode('utf-8')=="":
#                         #     finalReturn = finalReturn + row[0 + (i * 3)] + "\t" + "_" + "\t" + "_" + "\n"
#                         #     parkingLotNames = parkingLotNames + row[0 + (i * 3)] + "\n"
#                         # elif row[0+(i*3)].encode('utf-8')=="":
#                         #     finalReturn = finalReturn + "_" + "\t" + row[1+(i*3)] + "\t" + row[2+(i*3)] + "\n"
#                         # else:
#                         #     finalReturn = finalReturn + row[0 + (i * 3)] + "\t" + row[1 + (i * 3)] + "\t" + row[2 + (i * 3)] + "\n"
#                         #     parkingLotNames = parkingLotNames + row[0 + (i * 3)] + "\n"
#                         finalReturn = finalReturn + row[0 + (i * 3)] + "\t" + "_" + "\t" + "_" + "\n"
#                         parkingLotNames = parkingLotNames + row[0 + (i * 3)] + "\n"
#                     elif len(row)%3==0 or (len(row)%3==1 and (i+1)*3<len(row)):
#                         if row[0 + (i * 3)].encode('utf-8') == "":
#                             finalReturn = finalReturn + "_" + "\t" + row[1 + (i * 3)] + "\t" + row[2 + (i * 3)] + "\n"
#                         else:
#                             finalReturn = finalReturn + row[0 + (i * 3)] + "\t" + row[1 + (i * 3)] + "\t" + row[2 + (i * 3)] + "\n"
#                             parkingLotNames = parkingLotNames + row[0 + (i * 3)] + "\n"
#                     else:
#                         print ("Row not acceptable")
#                 # print (finalReturn)
#                 # Print columns A, B and C, which correspond to indices 0, 1 and 2.
#                 # print('%s, %s, %s' % (row[0], row[1], row[2]))
#             finalReturn =finalReturn+"|"
#             parkingLotNames = parkingLotNames + "|"
#         # print(finalReturn)
#         if "batei" in table_name:
#             temp_file = open("BkUsersFile.txt").read()
#             if temp_file.decode('utf-8') != finalReturn:
#                 with open('BkUsersFile.txt', 'w') as testFile:
#                     testFile.write(finalReturn.encode('utf-8'))
#                 user_insertion_BK()
#         else:
#             temp_file = open("PlUsersFile.txt").read()
#             if temp_file.decode('utf-8') != finalReturn:
#                 with open('PlUsersFile.txt', 'w') as testFile:
#                     testFile.write(finalReturn.encode('utf-8'))
#                 user_insertion_PL()
#                 # print(parkingLotNames)
#         # return (finalReturn)
#
# def manage_data(conn,addr):
#     while 1:
#             print ("recieving...")
#             data = conn.recv(25)
#             if not data:
#                 # client closed the connection
#                 break
#             print (data)
#
#             appType = data.split(" ")[0][2:]
#             if appType == "0": #!!!!!!!!!!!!!  Batei Kneset App  !!!!!!!!!!!
#
#                 table_name = "batei_kneset"
#                 global finalReturnMemory
#                 if "get statuses" in data:
#                     print ("received get statuses BK command")
#                     statuses = get_statuses(table_name, data.split(" ")[3])
#                     print(statuses)
#                     print(str(len(statuses)))
#                     if len(str(len(statuses))) == 1:
#                         conn.send("00" + str(len(statuses)))
#                     elif len(str(len(statuses))) == 2:
#                         conn.send("0" + str(len(statuses)))
#                     else:
#                         conn.send(str(len(statuses)))
#                     data = conn.recv(12)
#                     print("data: " + data)
#                     if "received" in data:
#                         conn.send(statuses) #sends list of seat id's where status = 1
#                     break
#                 elif "get messages" in data:
#                     print ("received get messages BK command")
#                     messages = update_messages(data.split(" ")[3])
#                     print(len(messages))
#                     conn.send(str(len(messages)*2))
#                     data = conn.recv(12)
#                     print("data: " + data)
#                     if "received" in data:
#                         messages = messages.encode('utf-8')
#                         messages = messages+messages
#                         print (messages)
#                         conn.send(messages) #sends list of messages
#                     else:
#                         conn.send("000")
#                     break
#                 elif "get memory" in data:
#                     print("received get memory BK command")
#                     update_messages(data.split(" ")[3])
#                     messages = finalReturnMemory
#                     print(len(messages))
#                     if len(str(len(messages) * 2)) == 1:
#                         conn.send("00" + str(len(messages) * 2))
#                     elif len(str(len(messages) * 2)) == 2:
#                         conn.send("0" + str(len(messages) * 2))
#                     else:
#                         conn.send(str(len(messages) * 2))
#                     # conn.send(str(len(messages)*2))
#                     data = conn.recv(12)
#                     print("data: " + data)
#                     if "received" in data:
#                         messages = messages.encode('utf-8')
#                         messages = messages + messages
#                         print(messages)
#                         conn.send(messages)  # sends list of messages
#                     else:
#                         conn.send("000")
#                     break
#                 elif "get updates" in data:
#                     print ("received get updates BK command")
#                     allUsers = open("BkUsersFile.txt").read()
#                     splitUsers = allUsers.split("|")
#                     users=splitUsers[int(data.split(" ")[3])]
#                     users = users[:-1]
#                     userLines = users.splitlines()
#                     print(len(userLines))
#                     i=0
#                     joinedLines=''
#                     while (i+20)<len(userLines):
#                         joinedLines='\n'.join(userLines[i:i+20])+'\n'
#                         print(str(len(str(len(joinedLines)))))
#                         conn.send(str(len(str(len(joinedLines)*2+1))))
#                         data = conn.recv(12)
#                         print(str(len(joinedLines)))
#                         conn.send(str(len(joinedLines)*2+1))
#                         data = conn.recv(12)
#                         print("data: " + data)
#                         if "received" in data:
#                             messages = joinedLines
#                             messages = messages+"|"+messages
#                             print("i = " + str(i))
#                             print ("--- Updating Users ---")
#                             conn.send(messages) #sends list of users
#                             conn.recv(12)
#                             i=i+20
#                     joinedLines='\n'.join(userLines[i:])+'\n'
#                     print(str(len(str(len(joinedLines)))))
#                     conn.send(str(len(str(len(joinedLines)*2+1))))
#                     data = conn.recv(12)
#                     print(str(len(joinedLines)))
#                     conn.send(str(len(joinedLines)*2+1))
#                     data = conn.recv(12)
#                     print("data: " + data)
#                     if "received" in data:
#                         messages = joinedLines
#                         messages = messages+"|"+messages
#                         print("i = " + str(i))
#                         print ("--- Updating Users ---")
#                         conn.send(messages) #sends list of users
#                         conn.recv(12)
#                     conn.send('0')
#                     conn.recv(12)
#                 elif "get password" in data:
#                     print ("received get password BK command")
#                     query = "SELECT password FROM batei_kneset WHERE id = '" + data.split(" ")[3] + "'"
#                     password = db.execute(query)
#                     password = password.fetchone()
#                     conn.send(str(password[0]))
#                 else:
#                     print ("error: Unknown command BK")
#                     conn.send("Unknown command")
#                     time.sleep(1)
#
#             elif appType == "1": #!!!!!!!!!!!!!  Parking Lots App  !!!!!!!!!!!
#
#                 table_name = "parking_lots"
#                 global parkingLotNames
#                 if "get statuses" in data:
#                     print ("received get statuses PL command")
#                     statuses = get_statuses(table_name, data.split(" ")[3])
#                     print(statuses)
#                     print(str(len(statuses)))
#                     if len(str(len(statuses))) == 1:
#                         conn.send("00" + str(len(statuses)))
#                     elif len(str(len(statuses))) == 2:
#                         conn.send("0" + str(len(statuses)))
#                     else:
#                         conn.send(str(len(statuses)))
#                     data = conn.recv(12)
#                     print("data: " + data)
#                     if "received" in data:
#                         print("sending statuses")
#                         conn.send(statuses) #sends list of seat id's where status = 1
#                     break
#                 elif "get user" in data:
#                     print("received get user PL command")
#                     BKid = data.split(" ")[4]
#                     BKname = db.execute("SELECT name FROM parking_lots WHERE id= '" + BKid + "'").fetchone()
#                     parkingId = data.split(" ")[3]
#                     send=""
#                     allLines = open("takenSpots.txt").readlines()
#                     for i in range(0,len(allLines)):
#                         splitedLine = allLines[i].split("\t")
#                         if splitedLine[0].rstrip() == BKid and splitedLine[2] == get_parking_name_from_id(BKname,parkingId):
#                             send=splitedLine[1].strip()
#                             if len(str(len(send))) == 1:
#                                 conn.send("0" + str(len(send)))
#                             else:
#                                 conn.send(str(len(send)))
#                             conn.recv(12)
#                             conn.send(send)
#                             conn.recv(12)
#                             break
#                     if send=="":
#                         conn.send("00")
#                 elif "get updates" in data:
#                     print ("received get updates PL command")
#                     # update_users(table_name)
#                     allParkings = open("parkingUsers.txt").read()
#                     # splitUsers = parkingLotNames.split("|")
#                     splitUsers = allParkings.split("|")
#                     users=splitUsers[int(data.split(" ")[3])]
#                     users = users[:-1]
#                     userLines = users.splitlines()
#                     i=0
#                     joinedLines=''
#                     while (i+50)<len(userLines):
#                         joinedLines='\n'.join(userLines[i:i+50])+'\n'
#                         print(str(len(str(len(joinedLines)))))
#                         conn.send(str(len(str(len(joinedLines)*2+1))))
#                         data = conn.recv(12)
#                         print(str(len(joinedLines)))
#                         conn.send(str(len(joinedLines)*2+1))
#                         data = conn.recv(12)
#                         print("data: " + data)
#                         if "received" in data:
#                             messages = joinedLines
#                             messages = messages+"|"+messages
#                             print ("--- Updating Users ---")
#                             conn.send(messages) #sends list of users
#                             conn.recv(12)
#                             print("i = " + str(i))
#                             i=i+50
#                     joinedLines='\n'.join(userLines[i:])+'\n'
#                     print(str(len(str(len(joinedLines)))))
#                     conn.send(str(len(str(len(joinedLines) * 2 + 1))))
#                     data = conn.recv(12)
#                     print(str(len(joinedLines)))
#                     conn.send(str(len(joinedLines)*2+1))
#                     data = conn.recv(12)
#                     print("data: " + data)
#                     if "received" in data:
#                         messages = joinedLines
#                         messages = messages+"|"+messages
#                         print ("--- Updating Users ---")
#                         conn.send(messages) #sends list of users
#                         conn.recv(12)
#                     conn.send('0')
#                     conn.recv(12)
#                 elif "get lists" in data:
#                     print("received get lists command")
#                     PLid = data.split(" ")[4]
#                     listType = int(data.split(" ")[3])
#                     fileLines = ""
#
#                     fileName = "takenSpots.txt"
#                     with open(fileName, 'r') as currFile:
#                         fileLinesDay = currFile.readlines()
#                         tmpLines = ""
#                         for line in fileLinesDay:
#                             if line.split("\t")[0].rstrip() == PLid:
#                                 tmpLines += " ".join(line.split("\t")[1:]) + "\n"
#                         fileLinesDay = tmpLines.split("\n")
#
#                     if listType == 0:  # Month.
#                         fileName = "monthly_update_" + PLid + ".txt"
#                         with open(fileName, 'r') as currFile:
#                             fileLines = currFile.readlines()
#                             fileLines.append(str("\n" + datetime.today().strftime('%Y-%m-%d') + "\n"))
#                             fileLines+= fileLinesDay # Send also current day.
#                     elif listType == 1:  # Week.
#                         fileName = "weekly_update_" + PLid + ".txt"
#                         with open(fileName, 'r') as currFile:
#                             fileLines = currFile.readlines()
#                             fileLines.append(str("\n" + datetime.today().strftime('%Y-%m-%d') + "\n"))
#                             fileLines += fileLinesDay  # Send also current day.
#                     elif listType == 2:  # Day.
#                         fileLines=("\n" + datetime.today().strftime('%Y-%m-%d') + "\n").split("\n")
#                         fileLines += fileLinesDay  # Send also current day.
#
#                     i = 0
#                     joinedLines = ''
#                     while (i + 50) < len(fileLines):
#                         joinedLines = '\n'.join(fileLines[i:i + 50]) + '\n'
#                         print(str(len(str(len(joinedLines)))))
#                         conn.send(str(len(str(len(joinedLines) * 2 + 1))))
#                         data = conn.recv(12)
#                         print(str(len(joinedLines)))
#                         conn.send(str(len(joinedLines) * 2 + 1))
#                         data = conn.recv(12)
#                         print("data: " + data)
#                         if "received" in data:
#                             messages = joinedLines
#                             messages = messages + "|" + messages
#                             print("--- Sent List ---")
#                             conn.send(messages)  # sends list of statuses
#                             conn.recv(12)
#                             print("i = " + str(i))
#                             i = i + 50
#                     joinedLines = '\n'.join(fileLines[i:]) + '\n'
#                     print(str(len(str(len(joinedLines)))))
#                     conn.send(str(len(str(len(joinedLines) * 2 + 1))))
#                     data = conn.recv(12)
#                     print(str(len(joinedLines)))
#                     conn.send(str(len(joinedLines) * 2 + 1))
#                     data = conn.recv(12)
#                     print("data: " + data)
#                     if "received" in data:
#                         messages = joinedLines
#                         messages = messages + "|" + messages
#                         print("--- Sent List ---")
#                         conn.send(messages)  # sends list of statuses
#                         conn.recv(12)
#                     conn.send('0')
#                     conn.recv(12)
#                 elif "get password" in data:
#                     print ("received get password PL command")
#                     query = "SELECT password FROM parking_lots WHERE id = '" + data.split(" ")[3] + "'"
#                     password = db.execute(query)
#                     password = password.fetchone()
#                     conn.send(str(password[0]))
#                 else:
#                     print ("error: Unknown command PL")
#                     conn.send("Unknown command")
#                     time.sleep(1)
#     db.commit()
#
# parkingLotNames = ""
# finalReturnMemor=""
# if __name__ == '__main__':
#     db = sqlite3.connect('parkingUsers.db', check_same_thread=False)
#     db.text_factory = str
#     c = db.cursor()
#     # update_users()
#     reset_statuses("parking_lots")
#     reset_statuses("batei_kneset")
#     #update_users("parking_lots")
#     # user_insertion()
#     threading.Thread(target = schedule_all).start()
#
#     modem = GsmModem(PORT, BAUDRATE, smsReceivedCallbackFunc=handleSms)
#     modem.smsTextMode = False
#     modem.connect(PIN)
#     modem.deleteMultipleStoredSms()
#     send_all("parking_lots")
#     #send_msg("parking_lots","+972527335866")#"972527335866", "972546456643"
#     # modem.deleteMultipleStoredSms()
#     # modem.smsc = "+972521100059" #cellcom
#
#
#     # threading.Thread(target = schedule_all).start()
#
#     while 1:
#         s = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
#         print ("Socket created")
#         try:
#             s.bind((HOSTCLIENT,PORTCLIENT))
#         except socket.error as msg:
#             print ("Bind failed. Error code : " + str(msg) + " message " + msg[1])
#             sys.exit()
#         print ("Socket bind complete")
#         s.listen(10)
#         print ("Socket now listening")
#         while True:
#             conn, addr = s.accept() # sever connection
#             print ("Connected with " + addr[0] + ":" + str(addr[1]))
#             messageThread = threading.Thread(target = manage_data, args = (conn, addr,))
#             messageThread.start()
#
#
#     print('Waiting for SMS message...')
#     try:
#         modem.rxThread.join(2**31) # Specify a (huge) timeout so that it essentially blocks indefinitely, but still receives CTRL+C interrupt signal
#     finally:
#         modem.close();
#
#     db.commit()
#     # s.close()
#     atexit.register(db.close)