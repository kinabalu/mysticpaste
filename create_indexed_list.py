#!/usr/bin/python
from pymongo import Connection
import redis

mongo = Connection()
db = mongo.mysticpaste
collection = db.pastes

redis = redis.Redis()

for paste in collection.find({"privateFlag": False, "abuseCount": {"$lt": 2}}).sort("pasteIndex", -1):
    redis.rpush('pasteHistory', paste['pasteIndex'])
