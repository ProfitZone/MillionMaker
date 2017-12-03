package wealth.vo;

public class Report {
	
	private String date;
	
	private String stockName;
	
	private float ClosingPrice; 
	
	private float percentageChange;
	
	private float volumnePercentage;
	
	private float movementInNDays;
	
	private float movingAverage;
	
	private float changeAboveMA;

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStockName() {
		return stockName;
	}

	public void setStockName(String stockName) {
		this.stockName = stockName;
	}

	public float getClosingPrice() {
		return ClosingPrice;
	}

	public void setClosingPrice(float closingPrice) {
		ClosingPrice = closingPrice;
	}

	public float getPercentageChange() {
		return percentageChange;
	}

	public void setPercentageChange(float percentageChange) {
		this.percentageChange = percentageChange;
	}

	public float getVolumnePercentage() {
		return volumnePercentage;
	}

	public void setVolumnePercentage(float volumnePercentage) {
		this.volumnePercentage = volumnePercentage;
	}

	public float getMovementInNDays() {
		return movementInNDays;
	}

	public void setMovementInNDays(float movementInNDays) {
		this.movementInNDays = movementInNDays;
	}

	public float getMovingAverage() {
		return movingAverage;
	}

	public void setMovingAverage(float movingAverage) {
		this.movingAverage = movingAverage;
	}

	@Override
	public String toString() {
		return "Report [date=" + date + ", stockName=" + stockName
				+ ", ClosingPrice=" + ClosingPrice + ", percentageChange="
				+ percentageChange + ", volumnePercentage=" + volumnePercentage
				+ ", movementInNDays=" + movementInNDays + ", movingAverage="
				+ movingAverage + ", changeAboveMA=" + changeAboveMA + "]";
	}

	public float getChangeAboveMA() {
		return changeAboveMA;
	}

	public void setChangeAboveMA(float changeAboveMA) {
		this.changeAboveMA = changeAboveMA;
	}
	
}
