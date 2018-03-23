Title:  Fixed Length Modifier Bug

Tags:   

Status: 9

Seq:    2.6

Date:   2003-03-13

Body:

Corrected a bug that resulted in an unrecoverable error when a length variable modifier was longer than the starting variable. Modified logic so that the variable will be padded with zeros on the left in this situation.
