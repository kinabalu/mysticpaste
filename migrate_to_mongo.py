#!/usr/bin/python
from pymongo import Connection
import psycopg2
import pprint
import psycopg2.extras

conn = psycopg2.connect("host=localhost dbname=mysticpaste")

cursor = conn.cursor(cursor_factory=psycopg2.extras.RealDictCursor)

mongo = Connection()

cursor.execute("SELECT * FROM paste_items")

records = cursor.fetchall()

db = mongo.mysticpaste
collection = db.pastes
for record in records:
    n = {}
    if record['private_token']:
        n['itemId'] = record['private_token']
    else:
        n['itemId'] = str(record['item_id'])
    if record['content']:
        n['content'] = record['content']
    else:
        continue
    n['clientIp'] = record['client_ip']
    n['type'] = record['lang_type_cd']
    # n['abuseFlag'] = record['abuse_flag']
    n['abuseFlag'] = False
    n['_class'] = 'com.mysticcoders.mysticpaste.model.PasteItem'
    n['private'] = record['private_flg']
    n['timestamp'] = record['create_ts']

    # print "nprivate %s = rprivate %s = nabuse %s" % (n['private'], record['private_flg'], n['abuseFlag'])
    # if not n['abuseFlag'] and not n['private']:
    #     pprint.pprint(n)

    collection.insert(n)
