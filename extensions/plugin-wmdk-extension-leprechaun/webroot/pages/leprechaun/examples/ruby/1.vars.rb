#!/usr/bin/env ruby


a = 10
b = 1000

c = [ 1, 2, 3, 4]

cs = c.inject(0){|i, j| i+j}

now = Time.now