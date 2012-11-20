#!/usr/bin/python
from pymongo import Connection
import redis
import pprint

mongo = Connection()
db = mongo.mysticpaste
collection = db.pastes

redis = redis.Redis()

pasteIndex = redis.set("pasteIndex", 0)
print pasteIndex
for paste in collection.find({}).sort("timestamp", 1):
    pasteIndex = redis.incr("pasteIndex")
    collection.update({"itemId": paste['itemId']}, {"$set": {"pasteIndex": pasteIndex}})
    # pprint.pprint(paste)

print "Paste Index: %d" % pasteIndex
