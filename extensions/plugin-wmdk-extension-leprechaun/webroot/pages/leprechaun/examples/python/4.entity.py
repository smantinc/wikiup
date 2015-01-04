#/usr/bin/env python
# -*- coding: utf-8 -*-

from wk import entity

try:
	e = entity.getContextEntity(wkContext, 'tTestTable')
	
	e.offset = 0
	e.limit = 15
	
	list = e.getRelatives('all')
except:
	list = []
