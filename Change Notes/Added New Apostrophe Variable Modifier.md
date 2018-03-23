Title:  Added New Apostrophe Variable Modifier

Tags:   Minor Enhancement

Status: 9

Seq:    4.40

Date:   2015-11-12

Body:

A plain apostrophe placed in the variable modifiers string will now cause any HTML entities representing an apostrophe or a single quotation mark to be converted back to a normal ASCII/UTF straight single quote/apostrophe character. This can be useful when generating HTML for e-mails, since the HTML entities are sometimes dropped by e-mail clients.
