package org.brailleblaster.perspectives.braille.stylers;

import org.brailleblaster.perspectives.braille.Manager;
import org.brailleblaster.perspectives.braille.document.BrailleDocument;
import org.brailleblaster.perspectives.braille.eventQueue.Event;
import org.brailleblaster.perspectives.braille.eventQueue.EventFrame;
import org.brailleblaster.perspectives.braille.eventQueue.EventTypes;
import org.brailleblaster.perspectives.braille.eventQueue.ModelEvent;
import org.brailleblaster.perspectives.braille.eventQueue.ViewEvent;
import org.brailleblaster.perspectives.braille.mapping.elements.TextMapElement;
import org.brailleblaster.perspectives.braille.mapping.maps.MapList;
import org.brailleblaster.perspectives.braille.messages.Message;
import org.brailleblaster.perspectives.braille.messages.Sender;
import org.brailleblaster.perspectives.braille.viewInitializer.ViewInitializer;

public class TextUpdateHandler extends Handler {

	BrailleDocument document;

	public TextUpdateHandler(Manager manager, ViewInitializer vi, MapList list){
		super(manager, vi, list);
	
		document = manager.getDocument();
	}
	
	public void updateText(Message message){
		message.put("selection", tree.getSelection(list.getCurrent()));
		if(list.getCurrent().isMathML()){
			manager.dispatch(Message.createRemoveNodeMessage(list.getCurrentIndex(), list.getCurrent().end - list.getCurrent().start));
			message.put("diff", 0);
		}
		else {
			addUndoEvent();
			resetText(message);
		}
		manager.getArchiver().setDocumentEdited(true);
	}
	
	public void undoText(EventFrame f){
		while(!f.empty() && f.peek().getEventType().equals(EventTypes.Update)){
			ModelEvent ev = (ModelEvent)f.pop();
			list.setCurrent(ev.getListIndex());
			manager.dispatch(Message.createUpdateCursorsMessage(Sender.TREE));
			addRedoEvent();
			Message m = Message.createUpdateMessage(list.getCurrent().start, ev.getNode().getValue(), list.getCurrent().end - list.getCurrent().start);
			resetText(m);
			manager.dispatch(Message.createUpdateCursorsMessage(Sender.TREE));
		}
	}
	
	public void redoText(EventFrame f){
		while(!f.empty() && f.peek().getEventType().equals(EventTypes.Update)){
			ModelEvent ev = (ModelEvent)f.pop();
			list.setCurrent(ev.getListIndex());
			manager.dispatch(Message.createUpdateCursorsMessage(Sender.TREE));
			addUndoEvent();
			Message m = Message.createUpdateMessage(list.getCurrent().start, ev.getNode().getValue(), list.getCurrent().end - list.getCurrent().start);
			resetText(m);
			manager.dispatch(Message.createUpdateCursorsMessage(Sender.TREE));
		}
	}

	private void resetText(Message message){
		document.updateDOM(list, message);
		braille.updateBraille(list.getCurrent(), message);
		text.reformatText(list.getCurrent().n, message, manager);
		list.updateOffsets(list.getCurrentIndex(), message);
		list.checkList();
	}
	
	private void addUndoEvent(){
		manager.addUndoEvent(addEvent());
	}
	
	private void addRedoEvent(){
		manager.addRedoEvent(addEvent());
	}
	
	private EventFrame addEvent(){
		EventFrame f = new EventFrame();
		TextMapElement t = list.getCurrent();
		Event e = new ModelEvent(EventTypes.Update, t.n, vi.getStartIndex(), list.getCurrentIndex(), t.start, 
				t.brailleList.getFirst().start, tree.getItemPath());
		f.addEvent(e);
		
		return f;
	}
	
	public void undoEdit(EventFrame f){
		EventFrame frame = new EventFrame();
		while(!f.empty() && f.peek().getEventType().equals(EventTypes.Edit)){
			ViewEvent ev = (ViewEvent)f.pop();
			text.view.setCaretOffset(ev.getTextOffset());
			
			int start = ev.getTextOffset();
			int end = ev.getTextOffset() + ev.getText().length();
			//String replacedtext = text.view.getText(ev.getTextOffset(), ev.getTextOffset() + ev.getText().length());
			String replacedtext = text.view.getTextRange(ev.getTextOffset(), ev.getTextEnd() - ev.getTextOffset());
			
			frame.addEvent(new ViewEvent(EventTypes.Edit, start, end, 0, 0, replacedtext));
			text.undoEdit(ev.getTextOffset(), ev.getTextEnd() - ev.getTextOffset(), ev.getText());
		}
		
		manager.addRedoEvent(frame);
	}
	
	public void redoEdit(EventFrame f){
		EventFrame frame = new EventFrame();
		while(!f.empty() && f.peek().getEventType().equals(EventTypes.Edit)){
			ViewEvent ev = (ViewEvent)f.pop();
			text.view.setCaretOffset(ev.getTextOffset());
			
			int start = ev.getTextOffset();
			int end =  ev.getTextOffset() + ev.getText().length();
			String replacedText = text.view.getTextRange(ev.getTextOffset(), ev.getTextEnd() - ev.getTextOffset());
		
			frame.addEvent(new ViewEvent(EventTypes.Edit, start, end, 0,0, replacedText));
			text.undoEdit(ev.getTextOffset(), ev.getTextEnd() - ev.getTextOffset(), ev.getText());
		}
		
		manager.addUndoEvent(frame);
	}
}
