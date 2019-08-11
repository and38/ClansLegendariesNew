package me.libraryaddictfan.Utilities;

public enum ChatAction {
	CHAT_MESSAGE, SERVER_MESSAGE, ACTION_BAR;

	public byte getValue() {
		return (byte) ordinal();
	}

}
