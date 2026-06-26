package com.makeurpicks.domain;

public class ViewPickColumn {

	private String value;
	private String attribute;
	private String playerId;
	private String gameId;
	
	public enum Attributes {ns, w, l, dw, dl, h}

	public String getValue() {
		return value;
	}

//	public void setValue(String value) {
//		this.value = value;
//	}

	public String getAttribute() {
		return attribute;
	}

//	public void setAttribute(String attribute) {
//		this.attribute = attribute;
//	}

	public String getPlayerId() {
		return playerId;
	}

//	public void setPlayerId(String playerId) {
//		this.playerId = playerId;
//	}

	public String getGameId() {
		return gameId;
	}

//	public void setGameId(String gameId) {
//		this.gameId = gameId;
//	};
	
	public static ViewPickColumn asNotStarted(String gameId, String playerId)
	{
		ViewPickColumn pickColumn = new ViewPickColumn();
		pickColumn.attribute = Attributes.ns.toString();
		pickColumn.value = "-";
		pickColumn.gameId = gameId;
		pickColumn.playerId = playerId;
		return pickColumn;
	}
	
	public static ViewPickColumn asNoPick(String gameId, String playerId)
	{
		ViewPickColumn pickColumn = new ViewPickColumn();
		pickColumn.attribute = Attributes.l.toString();
		pickColumn.value = "X";
		pickColumn.gameId = gameId;
		pickColumn.playerId = playerId;
		return pickColumn;
	}
	
	public static ViewPickColumn asLoser(String gameId, String playerId, String teamName)
	{
		ViewPickColumn pickColumn = new ViewPickColumn();
		pickColumn.attribute = Attributes.l.toString();
		pickColumn.value = teamName;
		pickColumn.gameId = gameId;
		pickColumn.playerId = playerId;
		return pickColumn;
	}
	
	public static ViewPickColumn asDoubleLoser(String gameId, String playerId, String teamName)
	{
		ViewPickColumn pickColumn = new ViewPickColumn();
		pickColumn.attribute = Attributes.dl.toString();
		pickColumn.value = teamName;
		pickColumn.gameId = gameId;
		pickColumn.playerId = playerId;
		return pickColumn;
	}
	
	public static ViewPickColumn asDoubleWinner(String gameId, String playerId, String teamName)
	{
		ViewPickColumn pickColumn = new ViewPickColumn();
		pickColumn.attribute = Attributes.dw.toString();
		pickColumn.value = teamName;
		pickColumn.gameId = gameId;
		pickColumn.playerId = playerId;
		return pickColumn;
	}
	
	public static ViewPickColumn asWinner(String gameId, String playerId, String teamName)
	{
		ViewPickColumn pickColumn = new ViewPickColumn();
		pickColumn.attribute = Attributes.w.toString();
		pickColumn.value = teamName;
		pickColumn.gameId = gameId;
		pickColumn.playerId = playerId;
		return pickColumn;
	}
	
	public static ViewPickColumn asColumnHeader(String playerId, int wins)
	{
		ViewPickColumn pickColumn = new ViewPickColumn();
		pickColumn.attribute = Attributes.h.toString();
		pickColumn.value = new StringBuilder(playerId).append("(").append(String.valueOf(wins)).append(")").toString();
		return pickColumn;
	}
	
	public static ViewPickColumn asRowHeader(String favteam, String dogTeam)
	{
		ViewPickColumn pickColumn = new ViewPickColumn();
		pickColumn.attribute = Attributes.h.toString();
		pickColumn.value = new StringBuilder(favteam).append(" vs ").append(dogTeam).toString();
		return pickColumn;
		
	}
	public static ViewPickColumn asBlank()
	{
		ViewPickColumn pickColumn = new ViewPickColumn();
		pickColumn.value = "";
		return pickColumn;
	}
	
	
	
}
