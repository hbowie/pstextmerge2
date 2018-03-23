Title:  Ignore cells with rowspan/colspan > 1

Tags:   

Status: 9

Seq:    2.5

Date:   2003-02-13

Body:

When reading HTML tables, modified logic to bypass table cells where rowspan or colspan is greater than 1, on the assumption that this would not be part of a true data table.
