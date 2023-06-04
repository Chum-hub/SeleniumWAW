package org.example;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.time.Duration;

public class Frame extends JFrame {
    private WebDriver driver;
    private JLabel messageLabel1;
    private JLabel messageLabel2;
    private JLabel messageLabel3;
    private WebDriverWait wait;
    private JPanel panel;
    private JButton button;
    private JFormattedTextField userMessageField;
    private JFormattedTextField phoneNumberField;
    private MaskFormatter phoneMask;
    private String userMessage;
    private String recipientNumber;
    private String link;

    public Frame() throws ParseException {
        setBounds(Const.START_X, Const.START_Y, Const.WIDTH, Const.HEIGHT);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);
        phoneMask = new MaskFormatter(Const.PHONE_MASK);
        userMessageField = new JFormattedTextField("");
        userMessageField.setPreferredSize(new Dimension(150, 30));
        phoneNumberField = new JFormattedTextField(phoneMask);
        phoneNumberField.setPreferredSize(new Dimension(150, 30));

        panel = new JPanel();

        button = new JButton("WhatsApp");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                recipientNumber = phoneNumberField.getValue() + "";
                userMessage = userMessageField.getValue() + "";
                openWhatsAppWeb();
                phoneNumberField.setText(null);
                userMessageField.setText(null);
            }
        });

        messageLabel1 = new JLabel("");
        messageLabel2 = new JLabel("");
        messageLabel2.setBounds(40, 50, 90, 30);
        messageLabel3 = new JLabel("");
        messageLabel3.setBounds(150, 50, 90, 30);

        panel.add(messageLabel1);
        panel.add(button);
        panel.add(userMessageField);
        panel.add(phoneNumberField);
        panel.add(messageLabel2);
        panel.add(messageLabel3);
        add(panel);

        panel.setVisible(true);
        button.setVisible(true);
        messageLabel1.setVisible(true);
        userMessageField.setVisible(true);
        phoneNumberField.setVisible(true);
        setVisible(true);
    }

    private void pasteAndSendText() {
        //delay to evade loading banner
        try {
            Thread.sleep(4000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        driver.switchTo().activeElement().sendKeys(userMessage);
        driver.switchTo().activeElement().sendKeys(Keys.ENTER);
    }

    private void executeSearch() {
        link = Const.ADRESS + recipientNumber;
        driver.get(link);
        wait = new WebDriverWait(driver, Duration.ofSeconds(120));
    }

    private boolean isNumberEmpty() {
        return recipientNumber.length() != 12;
    }

    private boolean isNumberNotValid() {
        return !(recipientNumber.contains("972") && !isNumberEmpty());
    }

    private void openWhatsAppWeb() {
        if (!userMessage.isEmpty() && !isNumberEmpty() && !isNumberNotValid()) {
            messageLabel2.setText("");
            messageLabel3.setText("");
            System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
            driver = new ChromeDriver();
            driver.get("https://web.whatsapp.com/");
            wait = new WebDriverWait(driver, Duration.ofSeconds(30));
            if (isHeaderExist()) {
                messageLabel1.setText("logged!");
                executeSearch();
                pasteAndSendText();
            }

        } else {
            if (isNumberEmpty()) {
                messageLabel3.setText("number is not full!");
            } else if (isNumberNotValid()) {
                messageLabel3.setText("number is not valid!");
            } else {
                messageLabel3.setText("");
            }
            if (userMessage.isEmpty()) {
                messageLabel2.setText("message is empty!");
            } else {
                messageLabel2.setText("");
            }
        }
    }

    private boolean isHeaderExist() {
        try {
            WebElement header = wait.until(ExpectedConditions.visibilityOfElementLocated(By.tagName("header")));
            return header.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }
}
