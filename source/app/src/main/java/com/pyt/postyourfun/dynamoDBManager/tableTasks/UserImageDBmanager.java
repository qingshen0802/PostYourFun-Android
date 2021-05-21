package com.pyt.postyourfun.dynamoDBManager.tableTasks;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.pyt.postyourfun.dynamoDBClass.UserImageMapper;
import com.pyt.postyourfun.dynamoDBManager.DynamoDBManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Simon on 7/24/2015.
 */
public class UserImageDBmanager extends DynamoDBManager {
    private static String TAG = "UserImageDBManager";

    public static void insertUserImage(String transactionId, String userId, String imageId, String imageUrl, String datetime, String thumbUrl) {
        AmazonDynamoDBClient ddb = clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            UserImageMapper userImageMapper = new UserImageMapper();
            userImageMapper.setTransactionId(transactionId);
            userImageMapper.setImageUrl(imageUrl);
            userImageMapper.setImageId(imageId);
            userImageMapper.setDateTime(datetime);
            userImageMapper.setUserId(userId);
            userImageMapper.setOwned(true);
            userImageMapper.setImageThumbUrl(thumbUrl);
            mapper.save(userImageMapper);
        } catch (AmazonServiceException e) {
            clientManager.wipeCredentialsOnAuthError(e);
        }
    }

    public static List<UserImageMapper> getUserImages(String userId) {
        AmazonDynamoDBClient ddb = clientManager.ddb();
        DynamoDBMapper mapper = new DynamoDBMapper(ddb);

        try {
            DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
            Map<String, Condition> scanFilter = new HashMap<>();

            Condition scanCondition =
                    new Condition().withComparisonOperator(ComparisonOperator.EQ.toString()).withAttributeValueList(new AttributeValue().withS(userId));

            scanFilter.put("UserId", scanCondition);
            scanExpression.setScanFilter(scanFilter);

            List<UserImageMapper> result = mapper.scan(UserImageMapper.class, scanExpression);
            if (result.size() > 0) {
                return result;
            } else {
                return null;
            }
        } catch (AmazonServiceException e) {
            clientManager.wipeCredentialsOnAuthError(e);
        }
        return null;
    }
}
