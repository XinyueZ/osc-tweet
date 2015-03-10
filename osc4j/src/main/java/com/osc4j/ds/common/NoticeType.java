package com.osc4j.ds.common;

import java.io.Serializable;

public enum NoticeType implements Serializable{
	Null, //For error when unable to load feeds.
	AtMe,
	Comments
}
