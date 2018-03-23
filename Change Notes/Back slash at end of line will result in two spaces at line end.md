Title:  Back slash at end of line will result in two spaces at line end

Tags:   Minor Enhancement

Status: 9

Seq:    4.20

Date:   2013-06-09

Body:

The software normally drops trailing spaces at the end of a line, but a line ending with a space and a backslash will now result in two spaces appearing at the end of the output line, which will result in a line break being generated if converting Markdown to HTML.
