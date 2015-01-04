function sum(array)
{
	var s = 0;
	for(var key in array)
		s += array[key]
	return s;
}

var a = 10;
var b = 10 * 10 * 10;

var c = [1, 2, 3, 4, 5, 6, 7, 8, 9];
var cs = sum(c);

var now = new Date() * 1;