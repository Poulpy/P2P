package fr.uvsq.fsp.test;

import fr.uvsq.fsp.util.Checksum;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class TestChecksum {

    @Test
    public void testGetHash() throws Exception {
        Assert.assertEquals(Checksum.getHash("MD5", "admin"), Checksum.getHash("MD5", "admin"));
        Assert.assertEquals(Checksum.getHash("SHA-256", "admin"), Checksum.getHash("SHA-256", "admin"));
        Assert.assertNotEquals(Checksum.getHash("MD5", "admin"), Checksum.getHash("MD5", "ADMIN"));
    }
}

