Title:  Add Template Library

Tags:   Minor Enhancement

Status: 9

Seq:    3.1

Date:   2005-05-17

Body:

Added a template library. This defaults to a new templates folder in the PSTextMerge application folder. A new button on the Template tab allows you to select another folder to contain your collection of standard templates. Another new button on the Template tab allows you to open a template from the template library, rather than the folder where the input data file, or script file, resides. Recording a script that references a file from the template library will store the folder location as the template library, so that moving the template library will not later break scripts that have been recorded. In combination with the ability to reference the dataparent folder as a variable, and call out the data file name as a base file name (without a file extension), templates may now be written (and are supplied with the PSTextMerge standard distribution), that can be used with any appropriate data file, and will generate output in the data file's location, and with the data file's name (rather than hard-coding a specific output file name in each template file).
