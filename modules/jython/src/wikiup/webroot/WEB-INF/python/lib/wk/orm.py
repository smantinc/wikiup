#!/usr/bin/env python
# -*- coding: utf-8 -*-

from org.wikiup.database.orm import WikiupEntityManager
from org.wikiup.modules.jython.orm import PythonEntity


def getEntity(name, ctx=None, **selection):
    return PythonEntity(_getEntity(name, ctx, selection))


def query(name, relation, ctx=None, **selection):
    entity = _getEntity(name, ctx, selection, False)
    relatives = entity.getRelatives(relation, None)
    props = [str(i.getName()) for i in relatives.getProperties()]
    r = [dict([(j, str(i.get(j))) for j in props]) for i in relatives]
    if len(r) == 0:
        r = dict([(str(i.getName()), i.getObject()) for i in relatives.getProperties()])
    entity.release()
    return r


def _getEntity(name, ctx=None, selection=None, autoselect=True):
    entity = None
    if ctx is not None:
        entity = ctx.get(name)
    if entity is None:
        entity = (ctx or WikiupEntityManager.getInstance()).getEntity(name)
    if selection:
        for k in selection:
            entity.set(k, selection[k])
        if autoselect:
            entity.select()
    return entity