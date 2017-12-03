package wealth.vo;

public class Stock {
	
	private String name;
	
	private float openPrice;
	
	private float lowPrice;
	
	private float highPrice;
	
	private float closePrice;
	
	private float prevClosePrice;
	
	private int quantityTraded;
	
	private String OptionType;
	
	private int openInterest;
	
	private int changeInOI;
	
	public float getPrevClosePrice() {
		return prevClosePrice;
	}

	public void setPrevClosePrice(float prevClosePrice) {
		this.prevClosePrice = prevClosePrice;
	}

	public int getQuantityTraded() {
		return quantityTraded;
	}

	public void setQuantityTraded(int quantityTraded) {
		this.quantityTraded = quantityTraded;
	}

	private float changePercentage;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public float getOpenPrice() {
		return openPrice;
	}

	public void setOpenPrice(float openPrice) {
		this.openPrice = openPrice;
	}

	public float getLowPrice() {
		return lowPrice;
	}

	public void setLowPrice(float lowPrice) {
		this.lowPrice = lowPrice;
	}

	public float getHighPrice() {
		return highPrice;
	}

	public void setHighPrice(float highPrice) {
		this.highPrice = highPrice;
	}

	public float getClosePrice() {
		return closePrice;
	}

	public void setClosePrice(float closePrice) {
		this.closePrice = closePrice;
	}

	public float getChangePercentage() {
		return (closePrice-prevClosePrice)*100/prevClosePrice;
	}

	public void setChangePercentage(float changePercentage) {
		this.changePercentage = changePercentage;
	}

	public String getOptionType() {
		return OptionType;
	}

	public void setOptionType(String optionType) {
		OptionType = optionType;
	}

	public int getOpenInterest() {
		return openInterest;
	}

	public void setOpenInterest(int openInterest) {
		this.openInterest = openInterest;
	}

	public int getChangeInOI() {
		return changeInOI;
	}

	public void setChangeInOI(int changeInOI) {
		this.changeInOI = changeInOI;
	}

	@Override
	public String toString() {
		return "Stock [name=" + name + ", openPrice=" + openPrice
				+ ", lowPrice=" + lowPrice + ", highPrice=" + highPrice
				+ ", closePrice=" + closePrice + ", changePercentage="
				+ changePercentage + "]";
	}
	
	

}
