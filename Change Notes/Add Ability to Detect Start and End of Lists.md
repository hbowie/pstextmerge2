Title:  Add Ability to Detect Start and End of Lists

Tags:   New Feature

Status: 9

Seq:    3.2

Date:   2005-08-14

Body:

Added IFNEWLIST and IFENDLIST commands, that indicate the beginning and ending of a list of records, all having the same group identifier at the specified level. So whereas IFNEWGROUP indicates a new value in the group identifier field for a particular level, IFNEWLIST indicates the start of a new sequence of values. In general, IFNEWLIST and IFENDLIST can be used to insert begin list and end list tags when generating HTML.
