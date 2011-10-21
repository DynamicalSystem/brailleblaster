/* BrailleBlaster Braille Transcription Application
  *
  * Copyright (C) 2010, 2012
  * ViewPlus Technologies, Inc. www.viewplus.com
  * and
  * Abilitiessoft, Inc. www.abilitiessoft.com
  * All rights reserved
  *
  * This file may contain code borrowed from files produced by various 
  * Java development teams. These are gratefully acknoledged.
  *
  * This file is free software; you can redistribute it and/or modify it
  * under the terms of the Apache 2.0 License, as given at
  * http://www.apache.org/licenses/
  *
  * This file is distributed in the hope that it will be useful, but
  * WITHOUT ANY WARRANTY; without even the implied warranty of
  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE
  * See the Apache 2.0 License for more details.
  *
  * You should have received a copy of the Apache 2.0 License along with 
  * this program; see the file LICENSE.
  * If not, see
  * http://www.apache.org/licenses/
  *
  * Maintained by John J. Boyer john.boyer@abilitiessoft.com
*/

package org.brailleblaster.wordprocessor;

import org.eclipse.swt.*;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.layout.FormLayout;
import org.brailleblaster.BBIni;
import org.eclipse.swt.printing.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.custom.StyledText;
import nu.xom.*;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import org.liblouis.liblouisutdml;
import org.brailleblaster.util.Notify;
import java.io.File;
import org.daisy.printing.*;
import javax.print.PrintException;

class DocumentManager {

/**
 * This class manages each document in an MDI environment. It controls 
 * the braille View and the daisy View.
*/

final Display display;
final Shell documentWindow;
final int documentNumber;
final String docID;
int action;
int returnReason = 0;
FormLayout layout;
String documentName = null;
BBToolBar toolBar;
BBMenu menu;
AbstractView activeView;
DaisyView daisy;
BrailleView braille;
BBStatusBar statusBar;
boolean exitSelected = false;
Document doc = null;
String configFileList = null;
String tempPath;
String UTDMLTranslation = null;
String BRFTranslation = null;
liblouisutdml louisutdml;
String logFile = "Translate.log";
String settings;
int mode = 0;

/**
 * Constructor that sets things up for a new document.
*/
DocumentManager (Display display, int documentNumber, int action, 
String documentName) {
this.display = display;
this.documentNumber = documentNumber;
docID = new Integer (documentNumber).toString();
this.action = action;
this.documentName = documentName;
tempPath = BBIni.getTempFilesPath() + BBIni.getFileSep();
louisutdml = liblouisutdml.getInstance();
documentWindow = new Shell (display, SWT.SHELL_TRIM);
layout = new FormLayout();
documentWindow.setLayout (layout);
menu = new BBMenu (this);
toolBar = new BBToolBar (this);
daisy = new DaisyView (documentWindow);
braille = new BrailleView (documentWindow);
// activeView = (ProtoView)daisy;
statusBar = new BBStatusBar (documentWindow);
documentWindow.setSize (1000, 700);
documentWindow.layout(true, true);
documentWindow.open();
if (action == WP.OpenDocumentGetFile) {
fileOpen();
} else if (action == WP.DocumentFromCommandLine) {
openFirstDocument();
} else {
setWindowTitle ("Untitled");
}

while (!documentWindow.isDisposed() && returnReason == 0) {
if (!display.readAndDispatch())
display.sleep();
}
if (!BBIni.debugging()) {
documentWindow.dispose();
return;
}
switch (returnReason) {
case WP.DocumentClosed:
case WP.BBClosed:
finish();
break;
default:
//documentWindow.setVisible (false);
break;
}
}

/**
* Clean up before closing the document.
*/
void finish() {
documentWindow.dispose();
}

/**
* Checks if a return request  is valid and does any necessary 
* processing.
*/
boolean setReturn (int reason) {
switch (reason) {
case WP.SwitchDocuments:
if (WPManager.haveOtherDocuments()) {
returnReason = reason;
return true;
}
return false;
case WP.NewDocument:
returnReason = reason;
break;
case WP.OpenDocumentGetFile:
returnReason = reason;
break;
case WP.DocumentClosed:
returnReason = reason;
break;
case WP.BBClosed:
returnReason = reason;
break;
default:
break;
}
return true;
}

/**
 * This method is called to resume processing on this document after 
 * working on another.
 */
void resume() {
if (documentWindow.isDisposed())
return;
documentWindow.forceActive();
returnReason = 0;
}
 
void openFirstDocument() {
Builder parser = new Builder();
try {
doc = parser.build (documentName);
} catch (ParsingException e) {
new Notify ("Malformed document");
return;
}
catch (IOException e) {
new Notify ("Could not open " + documentName);
return;
}
setWindowTitle (documentName);
Element rootElement = doc.getRootElement();
walkTree (rootElement);
}

private void setWindowTitle (String pathName) {
int index = pathName.lastIndexOf (File.separatorChar);
if (index == -1) {
documentWindow.setText ("BrailleBlaster " + pathName);
} else {
documentWindow.setText ("BrailleBlaster " + pathName.substring (index + 
1));
}
}

void fileOpen () {
if (BBIni.debugging() && doc != null) {
returnReason = WP.OpenDocumentGetFile;
return;
}
Shell shell = new Shell (display, SWT.DIALOG_TRIM);
FileDialog dialog = new FileDialog (shell, SWT.OPEN);
dialog.setFilterExtensions (new String[] {"xml", "utd"});
dialog.setFilterNames (new String[] {"DAISY xml file", "DAISY file with UTDML"});
documentName = dialog.open();
shell.dispose();
if (documentName == null) {
new Notify ("File not found");
return;
}
String fileName = documentName;
Builder parser = new Builder();
try {
doc = parser.build (fileName);
} catch (ParsingException e) {
new Notify ("Malformed document");
return;
}
catch (IOException e) {
new Notify ("Could not open " + documentName);
return;
}
setWindowTitle (documentName);
Element rootElement = doc.getRootElement();
walkTree (rootElement);
}

private void walkTree (Node node) {
int maxlines = 20;
int numlines = 0;
Node newNode;
for (int i = 0; i < node.getChildCount(); i++) {
newNode = node.getChild(i);
if (newNode instanceof Element) {
walkTree (newNode);
}
else if (newNode instanceof Text) {
numlines++;
if (numlines == maxlines) {
return;
}
String value = newNode.getValue();
daisy.view.append (value);
}
}
}

void fileSave() {
placeholder();
}

void fileSaveAs () {
Shell shell = new Shell (display, SWT.DIALOG_TRIM);
FileDialog dialog = new FileDialog (shell, SWT.SAVE);
String saveTo = dialog.open();
shell.dispose();
if (saveTo == null) {
new Notify ("could not write to " + saveTo);
return;
}
if (BRFTranslation == null) {
new Notify ("There is no translated file to be saved.");
return;
}
FileInputStream inFile = null;
FileOutputStream outFile = null;
try {
inFile = new FileInputStream (BRFTranslation);
} catch (FileNotFoundException e) {
new Notify ("Could not open " + BRFTranslation);
return;
}
try {
outFile = new FileOutputStream (saveTo);
} catch (FileNotFoundException e) {
new Notify ("Could not open " + saveTo);
return;
}
byte[] buffer = new byte[1024];
int length = 0;
while (length != -1) {
try {
length = inFile.read (buffer, 0, buffer.length);
} catch (IOException e) {
new Notify ("Problem reading " + BRFTranslation);
break;
}
if (length == -1) {
break;
}
try {
outFile.write (buffer, 0, length);
} catch (IOException e) {
new Notify ("Problem writing to " + saveTo);
break;
}
}
try {
outFile.close();
} catch (IOException e) {
new Notify (saveTo + " could not be completed");
}
}

void showBraille() {
String line;
BufferedReader translation = null;
try {
translation = new BufferedReader (new FileReader 
(BRFTranslation));
} catch (FileNotFoundException e) {
new Notify ("Could not fine " + BRFTranslation);
}
for (int i = 0; i < 20; i++) {
try {
line = translation.readLine();
} catch (IOException e) {
new Notify ("Problem reading " + BRFTranslation);
return;
}
if (line == null) {
break;
}
braille.view.append (line + "\n");
}
try {
translation.close();
} catch (IOException e) {
}
}

void translate() {
if (doc == null) {
new Notify ("There is no open file.");
return;
}
configFileList = "preferences.cfg";
String docFile = tempPath + docID + "-tempdoc.xml";
BRFTranslation = tempPath + docID + "-doc.brl";
FileOutputStream writer = null;
try {
writer = new FileOutputStream (docFile);
} catch (FileNotFoundException e) {
new Notify ("could not open file for writing");
return;
}
Serializer outputDoc = new Serializer (writer);
try {
outputDoc.write (doc);
} catch (IOException e) {
new Notify ("Could not write to file");
return;
}
boolean result = louisutdml.translateFile (configFileList, docFile, 
BRFTranslation, logFile, settings, mode);
if (!result) {
new Notify ("Translation failed.");
return;
}
showBraille();
}

void fileEmbossNow () {
if (BRFTranslation == null) {
translate();
}
if (BRFTranslation == null) {
return;
}
Shell shell = new Shell (display, SWT.DIALOG_TRIM);
PrintDialog embosser = new PrintDialog (shell);
PrinterData data = embosser.open();
shell.dispose();
if (data == null || data.equals("")) {
return;
}
File translatedFile = new File (BRFTranslation);
PrinterDevice embosserDevice;
try {
embosserDevice = new PrinterDevice (data.name, true);
embosserDevice.transmit (translatedFile);
} catch (PrintException e) {
new Notify ("Could not emboss on "  + data.name);
}
}

void placeholder() {
new Notify ("This menu item is not yet implemented. Sorry.");
}

}

