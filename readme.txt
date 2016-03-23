@Student Names:
	_ Phan Van Trung: s3479773
	_ Duong Thanh Nhan: s3479765
	_ Tran Ngoc Thuc: s3446356
	
@Contribution:
	_ Tran Ngoc Thuc:
		+ GUI 
		+ Login 
		+ Register
		+ Chat on same/different PCs
	_ Duong Thanh Nhan
		+ Private Chat
		+ Group Chat
		+ Emoticons
		+ User Status
	_ Phan Van Trung
		+ File transfer
		+ Record Chat
		+ Block/unblock user
		+ Voice Chat

@Functionality and bugs:
	_ All functions in PA, CR, DI; some functions in HD part
	_ To invoke private chat with somebody, double click his/her nickname on the List Online pannel
	_ In File Transfer function:
		> Admin/User sends file to User, file is saved in ClientDownload folder
		> User sends file to Admin, file is saved in ServerDownload folder
	- To demonstrate on different PCs, change the default "localhost" server IP to the server PC's IP
				(IP could be found in many ways, and easy one is to look at the Server.java output console)
	_ In Chat Record function: public chat is recored (by user or admin)
	_ One user can only take part in at most one group chat and/or private chat.
	_ In Voice Chat function: voice-chat-starter could not actively close the voice chat. 
							The action need to be performed by the voice-chat-receiver.
	_ Did not do video chat
	_ For the first time run the project, it might show an error of "something not found" (for example: Top Container..).
			Please re-run the project if needed, this will fix the problem (if any).

@References:
	http://stackoverflow.com/questions/13893534/adding-a-password-field-gui
	http://cs.lmu.edu/~ray/notes/javanetexamples/
	http://docs.oracle.com/javase/7/docs/api/javax/swing/JFileChooser.html
	https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
	http://www.rgagnon.com/javadetails/java-0542.html
	http://www.java2s.com/Code/Java/Network-Protocol/TransferafileviaSocket.htm
	http://stackoverflow.com/questions/22993918/how-to-include-emoticons-in-chatserver
	http://stackoverflow.com/questions/21159125/java-app-on-voice-calling
	http://stackoverflow.com/questions/4262669/refresh-jlist-in-a-jframe
	http://www.tutorialspoint.com/java/java_networking.htm
	https://www.youtube.com/watch?v=BsJiO6ergt0
	http://stackoverflow.com/questions/22188986/java-swing-how-do-i-create-a-jtextpane-with-multiple-icons-on-the-same-line
	http://www.ownedcore.com/forums/general/programming/381999-multithreading-sockets-file-transfer-client-server-java.html