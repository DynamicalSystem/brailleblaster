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
  * Maintained by Keith Creasy <kcreasy@aph.org>, Project Manager
*/

package org.brailleblaster.userHelp;

/**
 * This class contains constants that are used in other word processor 
 * classes.
 */

class WP {

// Document-Handling 

static final int NewDocument = 1;
static final int OpenDocumentGetFile = 2;
static final int ImportDocument = 3;
static final int SwitchDocuments = 4;
static final int DocumentFromCommandLine = 5;
static final int DocumentClosed = 6;
static final int BBClosed = 7;
static final int OpenDocumentGetRecent = 8;
static final int SwitchToRecentDoc = 9;

// Help 

static final int AboutBB = 1;
static final int HelpInfo = 2;
static final int ReadTutorial = 3;
static final int ReadManuals = 4;
static final int CheckUpdates = 5;

WP() {}

}


