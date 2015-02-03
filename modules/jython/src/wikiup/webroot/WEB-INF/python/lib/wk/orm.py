#!/usr/bin/env python
# -*- coding: utf-8 -*-

from org.wikiup.database.orm import WikiupEntityManager
from org.wikiup.modules.jython.orm import PythonEntity


def getEntity(name, condition=None, ctx=None):
    return PythonEntity(_getEntity(name, condition, ctx))


def query(name, relation, condition=None ,ctx=None):
    entity = _getEntity(name, condition, ctx, autoselect=False)
    relatives = entity.getRelatives(relation, None)
    props = [str(i.getName()) for i in relatives.getAttributes()]
    r = [dict([(j, str(i.getAttribute(j))) for j in props]) for i in relatives.getChildren()]
    entity.release()
    return r


def _getEntity(name, condition=None, ctx=None, autoselect=True):
    entity = (ctx or WikiupEntityManager.getInstance()).getEntity(name)
    if condition:
        for k in condition:
            entity.set(k, condition[k])
        if autoselect:
            entity.select()
    return entity