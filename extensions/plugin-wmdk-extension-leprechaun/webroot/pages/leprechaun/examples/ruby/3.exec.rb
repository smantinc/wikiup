#/usr/bin/env ruby
# -*- coding: utf-8 -*-

resp = java.library.path

wkContext.contentType = 'text/plain; charset=utf-8'

begin
out.write(resp.to_s)
rescue
end