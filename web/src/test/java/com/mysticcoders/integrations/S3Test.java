/*
 * Created by IntelliJ IDEA.
 * User: kinabalu
 * Date: Nov 14, 2009
 * Time: 4:40:01 PM
 */
package com.mysticcoders.integrations;

import org.jets3t.service.S3Service;
import org.jets3t.service.S3ServiceException;
import org.jets3t.service.acl.AccessControlList;
import org.jets3t.service.acl.GranteeInterface;
import org.jets3t.service.acl.GroupGrantee;
import org.jets3t.service.acl.Permission;
import org.jets3t.service.impl.rest.httpclient.RestS3Service;
import org.jets3t.service.model.S3Bucket;
import org.jets3t.service.model.S3Object;
import org.jets3t.service.model.S3Owner;
import org.jets3t.service.security.AWSCredentials;
import org.jets3t.service.utils.ServiceUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

public class S3Test {

    String awsAccessKey = "1SFRX9V8DVRNMABD7102";
    String awsSecretKey = "dc0uPXzMWwxPnJgLVU8Kwhq1PTAcoy7fMt+sWJHd";

    S3Service s3Service;

    @Test
    public void testListBuckets() {
        try {
// List all your buckets.
            S3Bucket[] buckets = s3Service.listAllBuckets();

// List the object contents of each bucket.
            for (int b = 0; b < buckets.length; b++) {
                System.out.println("Bucket '" + buckets[b].getName() + "' contains:");

                // List the objects in this bucket.
                S3Object[] objects = s3Service.listObjects(buckets[b]);

                // Print out each object's key and size.
                for (int o = 0; o < objects.length; o++) {
                    System.out.println(" " + objects[o].getKey() + " (" + objects[o].getContentLength() + " bytes)");
                }
            }

        } catch (S3ServiceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testAclIssues() {
        try {
            S3Bucket bucket = s3Service.getBucket("mysticpaste");

            AccessControlList acl = s3Service.getObjectAcl(bucket, "waldo.jpg");

            acl.grantPermission(GroupGrantee.AUTHENTICATED_USERS, Permission.PERMISSION_FULL_CONTROL);
            acl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_FULL_CONTROL);
            System.out.println("acl:"+acl);
            s3Service.putObjectAcl("mysticpaste", "waldo.jpg", acl);

        } catch(S3ServiceException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUploadObject() {

        InputStream is = getClass().getClassLoader()
                .getResourceAsStream("s3/waldo.jpg");

        S3Object uploadObject = new S3Object("waldo.jpg");

//            AccessControlList bucketAcl = s3Service.getBucketAcl(s3Bucket);

        uploadObject.setAcl(AccessControlList.REST_CANNED_PUBLIC_READ);

        try {
            S3Bucket s3Bucket =
                    s3Service.getBucket("mysticpaste");

            byte[] md5Hash = ServiceUtils.computeMD5Hash(is);
            is.reset();

            uploadObject.setDataInputStream(is);
            uploadObject.setContentLength(is.available());
            uploadObject.setContentType("image/jpeg");
//            uploadObject.setMd5Hash(md5Hash);

//            System.out.println("Hash value: " + uploadObject.getMd5HashAsHex());
            s3Service.putObject(s3Bucket, uploadObject);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (S3ServiceException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDeleteObject() {
        try {
            S3Bucket s3Bucket =
                    s3Service.getBucket("mysticpaste");
            System.out.println("s3Service.accountOwner:"+s3Service.getAccountOwner());

            AccessControlList acl = new AccessControlList();
            acl.setOwner(s3Bucket.getOwner());
            acl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_FULL_CONTROL);

            s3Service.putObjectAcl("mysticpaste", "waldo.jpg", acl);
            s3Service.deleteObject(s3Bucket, "waldo.jpg");

        } catch (S3ServiceException e) {
            e.printStackTrace();
        }

    }

    @Test
    public void makePublicBucket() {
        try {
            S3Bucket s3Bucket =
                    s3Service.getBucket("mysticpaste");

// Retrieve the bucket's ACL and modify it to grant public access,
// ie READ access to the ALL_USERS group.
            AccessControlList bucketAcl = s3Service.getBucketAcl(s3Bucket);
            bucketAcl.grantPermission(GroupGrantee.ALL_USERS, Permission.PERMISSION_FULL_CONTROL);

// Update the bucket's ACL. Now anyone can view the list of objects in this bucket.
            s3Bucket.setAcl(bucketAcl);
            s3Service.putBucketAcl(s3Bucket);
            System.out.println("View bucket's object listing here: http://s3.amazonaws.com/"
                    + s3Bucket.getName());
        } catch (S3ServiceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

    @Test
    public void makePrivateBucket() {
        try {
            S3Bucket s3Bucket =
                    s3Service.getBucket("mysticpaste");

            s3Bucket.setAcl(AccessControlList.REST_CANNED_PRIVATE);
            AccessControlList acl = AccessControlList.REST_CANNED_PRIVATE;
            acl.setOwner(s3Bucket.getOwner());
            System.out.println("s3Owner is:" + s3Bucket.getOwner());
            S3Owner s3Owner = new S3Owner();
            System.out.println("ACL for mysticpaste bucket:" + s3Bucket.getAcl());
            s3Service.putBucketAcl(s3Bucket);
            System.out.println("View bucket's object listing here: http://s3.amazonaws.com/"
                    + s3Bucket.getName());
        } catch (S3ServiceException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
    /*
System.out.println("Hash value: " + objectWithHash.getMd5HashAsHex());
If you do not use these constructors, you should always set the Content-MD5 header value yourself before you upload an object. JetS3t provides the ServiceUtils#computeMD5Hash method to calculate the hash value of an input stream or byte array.

ByteArrayInputStream dataIS = new ByteArrayInputStream(
        "Here is my data".getBytes(Constants.DEFAULT_ENCODING));
byte[] md5Hash = ServiceUtils.computeMD5Hash(dataIS);
dataIS.reset();

stringObject = new S3Object("MyData");
stringObject.setDataInputStream(dataIS);
stringObject.setMd5Hash(md5Hash);

     */

    @Before
    public void setUp() {
        AWSCredentials awsCredentials =
                new AWSCredentials(awsAccessKey, awsSecretKey);

        try {
            s3Service = new RestS3Service(awsCredentials);
        } catch (S3ServiceException e) {
            e.printStackTrace();
        }
    }

}