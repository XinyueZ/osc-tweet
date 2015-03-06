package com.osc4j.ds.personal;


import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public final class ObjectReply implements Serializable {
	@SerializedName("objectName")
	private String mObjectName;
	@SerializedName("objectBody")
	private String mObjectBody;

	public ObjectReply(String objectName, String objectBody) {
		mObjectName = objectName;
		mObjectBody = objectBody;
	}


	public String getObjectName() {
		return mObjectName;
	}

	public String getObjectBody() {
		return mObjectBody;
	}
}
