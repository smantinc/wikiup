#!/usr/bin/env python
# -*- coding: utf-8 -*-

from org.wikiup.database.orm import WikiupEntityManager
from org.wikiup.modules.jython.orm import PythonEntity

def getEntity(name, ctx = None):
	return PythonEntity(_getEntity(name, ctx))

def query(name, condition = {} ,ctx = None):
	ent = _getEntity(name, ctx)
	for k in condition:
		ent.set(k, condition[k])
	ent.select()
	return PythonEntity(ent)

def _getEntity(name, ctx):
	return ctx.getEntity(name) if ctx is not None else WikiupEntityManager.getInstance().getEntity(name)