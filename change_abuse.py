#!/usr/bin/python
from pymongo import Connection
import pprint

mongo = Connection()

db = mongo.mysticpaste
collection = db.pastes

for paste in collection.find({"abuseCount": True}):
    pprint.pprint(paste)