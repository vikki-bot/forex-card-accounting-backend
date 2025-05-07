package com.forexcard.dto;

import java.time.LocalDateTime;

import com.forexcard.model.Transaction;

public class TransactionDTO {

    private Long id;
    private LocalDateTime date;
    private double amount;
    private String merchant;
    private String status;
    private double currentBalance;
    private double deductAmount;
    private String orderId;
    private String currencyName;
    private String cardNumber;

    public TransactionDTO(Transaction transaction) {
        this.id = transaction.getId();
        this.date = transaction.getDate();
        this.amount = transaction.getAmount();
        this.merchant = transaction.getMerchant();
        this.status = transaction.getStatus();
        this.currentBalance = transaction.getCurrentBalance();
        this.deductAmount = transaction.getDeductAmount();
        this.orderId = transaction.getOrderId();

        if (transaction.getCurrency() != null) {
            this.currencyName = transaction.getCurrency().getName();
        }

        if (transaction.getForexCard() != null) {
            this.cardNumber = transaction.getForexCard().getCardNumber(); // Assuming `getCardId()` exists
        }
    }

    // Getters and setters for all fields

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(double currentBalance) {
        this.currentBalance = currentBalance;
    }

    public double getDeductAmount() {
        return deductAmount;
    }

    public void setDeductAmount(double deductAmount) {
        this.deductAmount = deductAmount;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCurrencyName() {
        return currencyName;
    }

    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }
}
