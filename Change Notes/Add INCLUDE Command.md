Title:  Add INCLUDE Command

Tags:   New Feature

Status: 9

Seq:    3.2

Date:   2005-07-19

Body:

Added an INCLUDE Command to Template processing. This command allows you to include text from another file into the output stream being generated by the template. The included text is not processed in any way, but is simply copied to the output file(s) being generated. This does allow output from a previous step in a script to be included in the output generated by a later step. If an include file is not found, then it will simply be skipped and processing will continue, with a log message to note the event.