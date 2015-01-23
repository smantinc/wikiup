#/usr/bin/env python
# -*- coding: utf-8 -*-

from wk import orm

try:
	e = orm.getEntity('tTestTable', wkContext)
	
	e.offset = 0
	e.limit = 15
	
	list = e.getRelatives('all')
except:
	list = []
