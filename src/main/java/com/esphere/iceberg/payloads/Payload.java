package com.esphere.iceberg.payloads;

import java.io.Serializable;

public abstract class Payload implements Serializable {

	private static final long serialVersionUID = -6418069436295229517L;

	public abstract Object getContent();
}
