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

package org.brailleblaster.abstractClasses;

import java.util.logging.Logger;

import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import org.brailleblaster.BBIni;
import org.brailleblaster.perspectives.braille.Manager;
import org.brailleblaster.perspectives.braille.messages.Message;
import org.brailleblaster.perspectives.braille.messages.Sender;
import org.eclipse.swt.*;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;

public abstract class AbstractView {
	public StyledText view;
	public boolean hasFocus = false;
	public boolean hasChanged = false;
	protected int total;
	protected int charWidth;
	protected int spaceBeforeText, spaceAfterText;
	public int positionFromStart, cursorOffset, words;
	public static int currentLine;
	protected boolean locked;
	protected static int currentAlignment;
	protected static int topIndex;
	protected Group group;
	protected Manager manager;
	protected static Logger logger = BBIni.getLogger();
	public AbstractView() {
	}

	public AbstractView(Manager manager, Group group, int left, int right, int top, int bottom) {
		this.manager = manager;
		this.group = group;
		view = new StyledText(group, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		setLayout(left, right, top, bottom);
		view.addModifyListener(viewMod);
	}

	// Better use a ModifyListener to set the change flag.
	ModifyListener viewMod = new ModifyListener() {
		@Override
		public void modifyText(ModifyEvent e) {
			hasChanged = true;
		}
	};
	
	protected void setLayout(int left, int right, int top, int bottom){
		FormData location = new FormData();
		location.left = new FormAttachment(left);
		location.right = new FormAttachment(right);
		location.top = new FormAttachment(top);
		location.bottom = new FormAttachment(bottom);
		view.setLayoutData(location);
	}
	
	public void incrementCurrent(){
		Message message = Message.createIncrementMessage();
		manager.dispatch(message);
		setViewData(message);
	}
	
	public void decrementCurrent(){
		Message message = Message.createDecrementMessage();
		manager.dispatch(message);
		setViewData(message);
	}
	
	protected int getFontWidth(){
		GC gc = new GC(this.view);
		FontMetrics fm =gc.getFontMetrics();
		gc.dispose();
		return fm.getAverageCharWidth();
	}
	
	protected int getFontHeight(){
		GC gc = new GC(this.view);
		FontMetrics fm =gc.getFontMetrics();
		gc.dispose();
		return fm.getHeight();
	}
	
	protected Element getBrlNode(Node n){
		Element e = (Element)n.getParent();
		int index = e.indexOf(n);
		if(index != e.getChildCount() - 1){
			if(((Element)e.getChild(index + 1)).getLocalName().equals("brl"))
				return (Element)e.getChild(index + 1);
		}
		
		return null;
	}
	
	public int getWordCount(String text){		
		String [] tokens = text.split(" ");
		return tokens.length;
	}
	
	protected void setListenerLock(boolean setting){
		locked = setting;
	}
	
	protected boolean getLock(){
		return locked;
	}
	
	public void setcharWidth(){
		charWidth = getFontWidth();
	}
	
	public void setTopIndex(int line){
		setListenerLock(true);
		view.setTopIndex(line);
		topIndex = line;
		setListenerLock(false);
	}
	
	protected void recreateView(Group group, int left, int right, int top, int bottom){
		view.dispose();
		view = new StyledText(group, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		view.addModifyListener(viewMod);
		setLayout(left, right, top, bottom);
		view.getParent().layout();
	}
	
	public void positionScrollbar(int topIndex){
		setListenerLock(true);
		group.setRedraw(false);
		view.setTopIndex(topIndex);
		group.setRedraw(true);
		group.getDisplay().update();
		setListenerLock(false);
	}
	
	protected void sendStatusBarUpdate(int line){
		String statusBarText = "";
		String page = manager.getCurrentPrintPage();
		if(page != null){
		
			statusBarText += "Page: " + page + " | ";
		}
		statusBarText += "Line: " + String.valueOf(line + 1) + " | ";
		
		if(view.getLineIndent(line) > 0){
			statusBarText += " Indent: " + ((view.getLineIndent(line) / charWidth)) + " | "; 
		}
		
		if(view.getLineAlignment(line) != SWT.LEFT){
			if(view.getLineAlignment(line) == SWT.CENTER)
				statusBarText += " Alignment: Center" + " | ";
			else if(view.getLineAlignment(line) == SWT.RIGHT)
				statusBarText += " Alignment: Right" + " | ";
		}
		
		Message statusMessage = Message.createUPdateStatusbarMessage(statusBarText + " Words: " + words);
		manager.dispatch(statusMessage);
		currentLine = view.getLineAtOffset(view.getCaretOffset());
	}
	
	public void checkStatusBar(Sender sender){
		if(!getLock()){
			if(topIndex != view.getTopIndex()){
				topIndex = view.getTopIndex();
				Message scrollMessage = Message.createUpdateScollbarMessage(sender, view.getOffsetAtLine(topIndex));
				manager.dispatch(scrollMessage);
			}
		}
	}
	
	public void insertText(int start, String text){
		setListenerLock(true);
		int originalPosition = view.getCaretOffset();
		view.setCaretOffset(start);
		view.insert(text);
		view.setCaretOffset(originalPosition);
		setListenerLock(false);
	}
	
	protected boolean isElement(Node n){
		return (n instanceof Element);
	}
	
	protected boolean isText(Node n){
		return (n instanceof Text);
	}
	
	public void setCursorOffset(int offset){
		cursorOffset = offset;
	}
	
	public int getCursorOffset(){
		return cursorOffset;
	}
	
	protected abstract void setViewData(Message message);
}
