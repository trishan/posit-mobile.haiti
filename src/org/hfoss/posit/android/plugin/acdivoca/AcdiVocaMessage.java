/*
 * File: AcdiVocaMessage.java
 * 
 * Copyright (C) 2011 The Humanitarian FOSS Project (http://www.hfoss.org)
 * 
 * This file is part of the ACDI/VOCA plugin for POSIT, Portable Open Search 
 * and Identification Tool.
 *
 * This plugin is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published 
 * by the Free Software Foundation; either version 3.0 of the License, or (at
 * your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, 
 * but WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU LGPL along with this program; 
 * if not visit http://www.gnu.org/licenses/lgpl.html.
 * 
 */

package org.hfoss.posit.android.plugin.acdivoca;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Date;

import com.j256.ormlite.field.DatabaseField;

import android.util.Log;

public class AcdiVocaMessage {
	
	public static final String TAG = "AcdiVocaMessage";
	
	public static final String ACDI_VOCA_PREFIX = "AV";
	public static final String ACK = "ACK";
	public static final String IDS = "IDS";
	public static final boolean EXISTING = true;

	// id is generated by the database and set on the object automagically
	@DatabaseField(generatedId = true)  int id;        // row_id in message table, generated automatically
	@DatabaseField(columnName = AcdiVocaDbHelper.MESSAGE_ID) int messageId = AcdiVocaDbHelper.UNKNOWN_ID;    // row Id of message in our Db
	@DatabaseField int beneficiaryId;         // row Id of beneficiary in our Db
	@DatabaseField String smsMessage;         // abbreviated Attr/val pairs
	@DatabaseField int msgStatus = -1;
	@DatabaseField Date message_created_at;
	@DatabaseField Date message_sent_at;
	@DatabaseField Date message_ack_at;
	
	
	@DatabaseField boolean isBulk; // Whether or not this is a bulk message
	@DatabaseField String distribution_id;
	
	private String rawMessage;	     // Attr/val pairs with long attribute names
	private String msgHeader =""; 
	@DatabaseField boolean existing = !EXISTING;  // Built from an existing message or, eg, a PENDING)
	private String numberSlashBatchSize;   // e.g 1/10  -- i.e., 1st of 10 messages in this batch
	
	public AcdiVocaMessage() {
		// Needed for ormlite
	}
	
	public AcdiVocaMessage(int messageId, int beneficiaryId, int msgStatus,  
			String rawMessage, String smsMessage, String msgHeader, boolean existing, boolean isBulk) {
		super();
		this.messageId = messageId;
		this.beneficiaryId = beneficiaryId;
		this.msgStatus = msgStatus;
		this.rawMessage = rawMessage;
		this.smsMessage = smsMessage;
		this.msgHeader = msgHeader;
		this.existing = existing;
		this.isBulk = isBulk;
	}
	
	/**
	 * Construct an instance from an Sms Message. This should be the
	 * converse of the toString() method, which returns this object as
	 * an Sms message. 
	 * @param smsText a string of the form AV=msgid,....  where the ...
	 * is either a comma-separated list of attr=val pairs or the ...
	 * is an ampersand separated list of Ids.  
	 * The message has the form:
	 * 
	 * 	AV=id,M:N,mi=mid,a1=v1,a2=v2,...
	 *
	 */
	public AcdiVocaMessage(String smsText) {
		Log.i(TAG, "Creating from smstext:" + smsText);
		String[] msgparts = smsText.split(AttributeManager.PAIRS_SEPARATOR);
//		for (int k = 0; k < msgparts.length; k++) {
//			Log.i(TAG, "msgpart " + k + " :" + msgparts[k]);
//		}
		String[] firstPair = msgparts[0].split(AttributeManager.ATTR_VAL_SEPARATOR);  // AV=id
		String[] thirdPair = msgparts[2].split(AttributeManager.ATTR_VAL_SEPARATOR);
		String avIdStr = firstPair[1];
		int avId = Integer.parseInt(avIdStr);
		String msgIdStr = thirdPair[1];
		int msgId = Integer.parseInt(msgIdStr);
		smsMessage = "";
		if (avId < 0 && avId != AcdiVocaDbHelper.UNKNOWN_ID) {
			messageId = avId * -1;
			beneficiaryId =  AcdiVocaDbHelper.UNKNOWN_ID;
			existing = true;
		} else {
			beneficiaryId = avId;
//			messageId =  AcdiVocaDbHelper.UNKNOWN_ID;
			messageId =  msgId;
			existing = true;
		} 
		
		// NOTE: We skip the first 3 pairs in constructing the actual SMS 
		// that was sent.  The first three pairs represent PREFIX information,
		// namely, AV=mid,N:m,mi=msgid ...
		//
		for (int k = 3; k < msgparts.length; k++) {
//			Log.i(TAG, "msgpart " + k + " :" + msgparts[k]);
			smsMessage += msgparts[k] + AttributeManager.PAIRS_SEPARATOR;
		}
		Log.i(TAG, "Resulting sms :" + smsMessage);
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getBeneficiaryId() {
		return beneficiaryId;
	}

	public void setBeneficiaryId(int beneficiaryId) {
		this.beneficiaryId = beneficiaryId;
	}

	
	public boolean isBulk() {
		return isBulk;
	}

	public void setBulk(boolean isBulk) {
		this.isBulk = isBulk;
	}

	public int getMsgStatus() {
		return msgStatus;
	}

	public void setMsgStatus(int msgStatus) {
		this.msgStatus = msgStatus;
	}

	public String getRawMessage() {
		return rawMessage;
	}

	public void setRawMessage(String rawMessage) {
		this.rawMessage = rawMessage;
	}

	public String getSmsMessage() {
		return smsMessage;
	}

	public void setSmsMessage(String smsMessage) {
		this.smsMessage = smsMessage;
	}

	public String getMsgHeader() {
		return msgHeader;
	}

	public void setMsgHeader(String msgHeader) {
		this.msgHeader = msgHeader;
	}
	
	public boolean isExisting() {
		return existing;
	}

	public void setExisting(boolean existing) {
		this.existing = existing;
	}
	

	public String getDistributionId() {
		return distribution_id;
	}

	public void setDistributionId(String distribution_id) {
		this.distribution_id = distribution_id;
	}

	public String getNumberSlashBatchSize() {
		return numberSlashBatchSize;
	}

	public void setNumberSlashBatchSize(String numberSlashBatchSize) {
		this.numberSlashBatchSize = numberSlashBatchSize;
	}

	/**
	 * Return an Sms Message.  The message has the following format:
	 * 		AV=id,M:N,mi=mid,a1=v1,a2=v2,...
	 * where id is either the message message ID or the beneficiary ID and
	 * where mid is the message ID.  So in some cases id=mid.
	 */
	@Override
	public String toString() {
		String message;
		if (beneficiaryId != AcdiVocaDbHelper.UNKNOWN_ID) {
			message = 
				AcdiVocaMessage.ACDI_VOCA_PREFIX 
			+ AttributeManager.ATTR_VAL_SEPARATOR 
			+ getBeneficiaryId() // For normal messages we use the beneficiary's row id, 1...N
			+ AttributeManager.PAIRS_SEPARATOR
			+ AttributeManager.ABBREV_MSG_NUMBER_SLASH_SIZE
			+ AttributeManager.ATTR_VAL_SEPARATOR
			+ getNumberSlashBatchSize()
			+ AttributeManager.PAIRS_SEPARATOR
			+ AttributeManager.ABBREV_MESSAGE_ID
			+ AttributeManager.ATTR_VAL_SEPARATOR 
			+ messageId 
			+ AttributeManager.PAIRS_SEPARATOR
			+ getSmsMessage();
		} else {
			message = 
				AcdiVocaMessage.ACDI_VOCA_PREFIX 
			+ AttributeManager.ATTR_VAL_SEPARATOR 
			+ getMessageId() * -1   // For Bulk messages we use minus the message id (e.g., -123)
			+ AttributeManager.PAIRS_SEPARATOR
			+ AttributeManager.ABBREV_MSG_NUMBER_SLASH_SIZE
			+ AttributeManager.ATTR_VAL_SEPARATOR
			+ getNumberSlashBatchSize()
			+ AttributeManager.PAIRS_SEPARATOR
			+ AttributeManager.ABBREV_MESSAGE_ID
			+ AttributeManager.ATTR_VAL_SEPARATOR 
			+ messageId 
			+ AttributeManager.PAIRS_SEPARATOR
			+ AttributeManager.ABBREV_DIST_ID
			+ AttributeManager.ATTR_VAL_SEPARATOR
			+ getDistributionId()
			+ AttributeManager.PAIRS_SEPARATOR
			+ getSmsMessage();
		}
		return message;
		//return msgHeader + AttributeManager.PAIRS_SEPARATOR + smsMessage;
	}
}
