package com.majd_alden.mailtestapp;

import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.Security;
import java.util.Date;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import com.google.api.client.util.Base64;
import com.google.api.services.gmail.model.Message;

public class Mail extends Authenticator {
    private String _user;
    private String _pass;

    private String[] _to;
    private String[] _cc;
    private String[] _Bcc;// add by chenjing 2017-04-13
    private String _from;

    private String _port;

    private String _sport;

    private String _host;

    private String _subject;
    private String _body;

    private boolean _auth;

    private boolean _debuggable;

    private Multipart _multipart;
    private String TAG = "Mail";

    public Mail() {
        // �ʼ���������QQ����:smtp.qq.com
        _host = "smtp.gmail.com";// SK
        // _host = "smtp.163.com";
        // smtp �˿�
        _port = "25";
        // socketfactory �˿�
        _sport = "587";// SK

        _user = "";
        _pass = "";
        _from = "";
        _subject = "";
        _body = "";

        _debuggable = false;
        // smtpĬ����֤
        _auth = true;

        _multipart = new MimeMultipart();

        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    public Mail(String user, String pass) {
        this();
        _user = user;
        _pass = pass;
    }

    static {
        Security.addProvider(new JSSEProvider());
    }

    public MimeMessage createMessage() throws Exception {
        Properties props = _setProperties();

        if (/*!_user.equals("") && !_pass.equals("") &&*/ _to.length > 0
                /*&& !_from.equals("")*/ && !_subject.equals("")
                && !_body.equals("")) {
//            Session session = Session.getInstance(props,this);
            Session session = Session.getInstance(props);
            MimeMessage msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress(_from));
            InternetAddress[] addressTo = new InternetAddress[_to.length];
            for (int i = 0; i < _to.length; i++) {
                addressTo[i] = new InternetAddress(_to[i]);
            }
            msg.setRecipients(MimeMessage.RecipientType.TO, addressTo);
            if (_cc != null && _cc.length > 0) {
                InternetAddress[] addressCC = new InternetAddress[_cc.length];
                for (int i = 0; i < _cc.length; i++) {
                    addressCC[i] = new InternetAddress(_cc[i]);
                }
                msg.setRecipients(MimeMessage.RecipientType.CC, addressCC);
            }
            if (_Bcc != null && _Bcc.length > 0) {
                InternetAddress[] addressBCC = new InternetAddress[_Bcc.length];
                for (int i = 0; i < _Bcc.length; i++) {
                    addressBCC[i] = new InternetAddress(_Bcc[i]);
                }
                msg.setRecipients(MimeMessage.RecipientType.BCC, addressBCC);
            }

            msg.setSubject(_subject);
            msg.setSentDate(new Date());
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(_body);
            _multipart.addBodyPart(messageBodyPart);
            msg.setContent(_multipart);
//            Transport transport = msg.getTransport("smtp");
            return msg;
        } else {
            return null;
        }
    }

    public Message createMessageWithEmail(MimeMessage emailContent)
            throws MessagingException, IOException,Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        emailContent.writeTo(buffer);
        byte[] bytes = buffer.toByteArray();
        String encodedEmail = Base64.encodeBase64URLSafeString(bytes);
        Message message = new Message();
        message.setRaw(encodedEmail);
        return message;
    }

    public Message createMessageWithEmail()
            throws MessagingException, IOException,Exception {
        return createMessageWithEmail(createMessage());
    }

    public void addAttachment(String filename) throws Exception {
        if (filename == null) { // Kingsley �ļ�Ϊ��ʱ����Ҫ���ͣ�����ᱨ��
            return;
        }
        MimeBodyPart messageBodyPart = new MimeBodyPart();
        FileDataSource source = new FileDataSource(filename);
        messageBodyPart.setDataHandler(new DataHandler(source));
        messageBodyPart.setFileName(filename);
        _multipart.addBodyPart(messageBodyPart);
    }

    @Override
    public PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(_user, _pass);
    }

    private Properties _setProperties() {
        Properties props = new Properties();

        // props.put("mail.smtp.host", _host);// SK

        if (_debuggable) {
            props.put("mail.debug", "true");
        }

        if (_auth) {
            props.put("mail.smtp.auth", "true");
        }

        // SK office365 fix

        props.put("mail.smtp.port", _sport);
        if (_host.equals("smtp.endothelix.com")) {
            _host = "smtp.office365.com";
        }
        if (_sport.equals("587")) {
            props.put("mail.smtp.starttls.enable", "true");
        } else {
            props.put("mail.smtp.socketFactory.port", _sport);
            props.put("mail.smtp.socketFactory.class",
                    "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");
        }
        props.put("mail.smtp.port", _port);
        props.put("mail.smtp.host", _host);
        props.put("mail.smtp.auth", "true");
        props.put("username", _user);
        props.put("password", _pass);


        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", _host);
        props.setProperty("mail.smtp.quitwait", "false");
        // SK
        return props;
    }

    public String get_user() {
        return _user;
    }

    public void set_user(String _user) {
        this._user = _user;
    }

    public String get_pass() {
        return _pass;
    }

    public void set_pass(String _pass) {
        this._pass = _pass;
    }

    public String[] get_to() {
        return _to;
    }

    public void set_to(String[] _to) {
        this._to = _to;
    }

    public String[] get_cc() {
        return _cc;
    }

    public void set_cc(String[] _cc) {
        this._cc = _cc;
    }

    public String get_from() {
        return _from;
    }

    public void set_from(String _from) {
        this._from = _from;
    }

    public String get_port() {
        return _port;
    }

    public void set_port(String _port) {
        this._port = _port;
    }

    public String get_sport() {
        return _sport;
    }

    public void set_sport(String _sport) {
        this._sport = _sport;
    }

    public String get_host() {
        return _host;
    }

    public void set_host(String _host) {
        this._host = _host;
    }

    public String get_subject() {
        return _subject;
    }

    public void set_subject(String _subject) {
        this._subject = _subject;
    }

    public String get_body() {
        return _body;
    }

    public void set_body(String _body) {
        this._body = _body;
    }

    public boolean is_auth() {
        return _auth;
    }

    public void set_auth(boolean _auth) {
        this._auth = _auth;
    }

    public boolean is_debuggable() {
        return _debuggable;
    }

    public void set_debuggable(boolean _debuggable) {
        this._debuggable = _debuggable;
    }

    public Multipart get_multipart() {
        return _multipart;
    }

    public void set_multipart(Multipart _multipart) {
        this._multipart = _multipart;
    }

    public String[] get_Bcc() {// add by chenjing 2017-04-13
        return _Bcc;
    }

    public void set_Bcc(String[] _Bcc) {// add by chenjing 2017-04-13
        this._Bcc = _Bcc;
    }
}


