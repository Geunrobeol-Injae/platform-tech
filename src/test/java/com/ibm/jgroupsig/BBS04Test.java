package com.ibm.jgroupsig;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class BBS04Test {

    /**
     * Will represent a BBS04 group from the point of view of the Issuer. I.e.,
     * it has an initialized grpKey and issKey.
     */
    private BBS04 groupMgr = null;

    /**
     * Will represent a BBS04 group from the point of view of a user. I.e.,
     * it has an initialized grpKey. When used to sign, member keys will be 
     * added.
     */    
    private BBS04 groupUser = null;
    
    BBS04Test() throws Exception {
	this.groupMgr = new BBS04();
	this.groupUser = new BBS04();
    }

    private MemKey addMember()
	throws IllegalArgumentException,
	       Exception
    {

	MemKey memkey = new MemKey(GS.BBS04_CODE);

	long mout1 = this.groupMgr.joinMgr(0, 0);
	if (mout1 == 0) {
	    return null;
	}

	long mout2 = this.groupMgr.joinMem(memkey, 1, mout1);

	return memkey;
	
    }

    private void setupFull()
	throws IllegalArgumentException,
	       Exception
    {
	this.groupMgr.setup();
	this.groupUser.setGrpKey(this.groupMgr.getGrpKey());
    }
    
    @Test
    public void creationOfGroupShouldSetInternalAttributes() {

        // assert statements
        assertTrue(this.groupMgr.getCode() == GS.BBS04_CODE,
		   "Unexpected group code.");
	assertTrue(this.groupMgr.getGroup() != 0,
		   "Unexpected group structure.");	
    }

    @Test
    public void setupCreatesGrpAndMgrKeys()
    	throws Exception
    {
    	this.setupFull();

    	assertTrue(this.groupMgr.getGrpKey() != null,
    		   "Unexpected issuer's group key.");
    	assertTrue(this.groupMgr.getMgrKey() != null,
    		   "Unexpected issuer's manager keys.");
    	assertTrue(this.groupMgr.getGml() != null,
    		   "Unexpected issuer's GML.");

    	assertTrue(this.groupUser.getGrpKey() != null,
    		   "Unexpected user's group key.");
    	assertTrue(this.groupUser.getMgrKey() == null,
    		   "Unexpected user's manager keys.");
    	assertTrue(this.groupUser.getGml() == null,
    		   "Unexpected user's GML.");	
	
    }

    @Test
    public void joinSeq()
    	throws Exception
    {
    	this.groupMgr.setup();
    	int seq = this.groupMgr.getJoinSeq();
    	assertTrue(seq == 1, "Unexpected join sequence steps.");
    }

    @Test
    public void joinStart()
    	throws Exception
    {
    	this.setupFull();
    	int start = this.groupMgr.getJoinStart();
    	assertTrue(start == 0, "Unexpected join start.");
    }

    @Test void addsAMember()
    	throws Exception {
    	this.setupFull();
    	MemKey memkey = this.addMember();
    	assertTrue(memkey.getObject() != 0, "Failed to join member.");
    }

    @Test void signBytesAndVerifyCorrectly()
    	throws Exception {
    	this.setupFull();
    	MemKey memkey = this.addMember();
    	Signature sig = this.groupUser.sign("Hello, World!".getBytes(), memkey);
    	boolean b = this.groupUser.verify(sig, "Hello, World!".getBytes());
    	assertTrue(b, "Signature should verify correctly.");
    }

    @Test void signBytesAndVerifyFails()
    	throws Exception {
    	this.setupFull();
    	MemKey memkey = this.addMember();
    	Signature sig = this.groupUser.sign("Hello, World!".getBytes(), memkey);
    	boolean b = this.groupUser.verify(sig, "Hello, Worlds!".getBytes());
    	assertFalse(b, "Signature should not verify correctly.");
    }

    @Test void signStringAndVerifyCorrectly()
    	throws Exception {
    	this.setupFull();
    	MemKey memkey = this.addMember();
    	Signature sig = this.groupUser.sign("Hello, World!", memkey);
    	boolean b = this.groupUser.verify(sig, "Hello, World!");
    	assertTrue(b, "Signature should verify correctly.");
    }

    @Test void signStringAndVerifyFails()
    	throws Exception {
    	this.setupFull();
    	MemKey memkey = this.addMember();
    	Signature sig = this.groupUser.sign("Hello, World!", memkey);
    	boolean b = this.groupUser.verify(sig, "Hello, Worlds!");
    	assertFalse(b, "Signature should not verify correctly.");
    }

    @Test void openSignature()
    	throws Exception {

    	this.setupFull();

    	/* Add members */
    	MemKey memkey1 = this.addMember();
	MemKey memkey2 = this.addMember();

    	/* Create sample signature */
    	Signature sig = this.groupUser.sign("Hello, World!", memkey2);

	/* Open the signature */
	IndexProof indexProof = this.groupMgr.open(sig);
	long index = indexProof.getIndex();
	
    	/* Signer is 1 */
    	assertTrue(index == 1, "Signer's index should be 1.");
	
    }

    @Test
    public void exportImportGrpKey()
    	throws Exception
    {
    	this.setupFull();
    	String sgrp = this.groupUser.getGrpKey().export();
    	GrpKey gpk = new GrpKey(this.groupUser.getCode(), sgrp);
    	assertTrue(gpk.getObject() != 0, "Unexpected imported group key.");
    }

    @Test
    public void exportImportMgrKey()
    	throws Exception
    {
    	this.setupFull();
    	String smgr = this.groupMgr.getMgrKey().export();
    	MgrKey gsk = new MgrKey(this.groupMgr.getCode(), smgr);
    	assertTrue(gsk.getObject() != 0, "Unexpected imported manager key.");
    }

    @Test
    public void exportImportMemKey()
    	throws Exception
    {
    	this.setupFull();
    	MemKey mem = this.addMember();
    	String smem = mem.export();
    	MemKey mem2 = new MemKey(this.groupUser.getCode(), smem);
    	assertTrue(mem.getObject() != 0, "Unexpected imported member key.");
    }

    @Test
    public void exportImportSignature()
    	throws Exception
    {
    	this.setupFull();
    	MemKey mem = this.addMember();
    	Signature sig = this.groupUser.sign("Hello, World!", mem);
    	String ssig = sig.export();
    	Signature sig2 = new Signature(this.groupUser.getCode(), ssig);
    	boolean b = this.groupUser.verify(sig2, "Hello, World!");
    	assertTrue(b, "Imported signature should verify correctly.");
    }

    @Test
    public void exportImportGml()
    	throws Exception
    {
    	this.setupFull();
    	MemKey mem = this.addMember();
    	String sgml = this.groupMgr.getGml().export();
	/* This should be refactored into a negative test that throws an
	   exception, as here no exception should be thrown ... */
    	Gml gml2 = new Gml(this.groupUser.getCode(), sgml);
    }    
    
    
}