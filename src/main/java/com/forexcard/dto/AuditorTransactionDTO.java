package com.forexcard.dto;

import java.time.LocalDateTime;

public class AuditorTransactionDTO {
    private LocalDateTime date; 
    private String name;
    private String cardNumber; 
    private String merchant;
    private String currencyName; 
    private double currentBalance;
    private double deductAmount;
    private double amount; 
    private String status;
    

    

    public AuditorTransactionDTO(LocalDateTime date, String name, String cardNumber, String merchant,
			String currencyName, double currentBalance, double deductAmount, double amount, String status) {
		super();
		this.date = date;
		this.name = name;
		this.cardNumber = cardNumber;
		this.merchant = merchant;
		this.currencyName = currencyName;
		this.currentBalance = currentBalance;
		this.deductAmount = deductAmount;
		this.amount = amount;
		this.status = status;
	}

	// Getters and Setters
    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

	public double getDeductAmount() {
		return deductAmount;
	}

	public void setDeductAmount(double deductAmount) {
		this.deductAmount = deductAmount;
	}

	public double getCurrentBalance() {
		return currentBalance;
	}

	public void setCurrentBalance(double currentBalance) {
		this.currentBalance = currentBalance;
	}
}
