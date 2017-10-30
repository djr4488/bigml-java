package org.bigml.binding.resources;

import org.bigml.binding.BigMLClient;
import org.bigml.binding.utils.CacheManager;
import org.bigml.binding.utils.Utils;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * Entry point to create, retrieve, list, update, and delete anomaly scores.
 *
 * Full API documentation on the API can be found from BigML at:
 * https://bigml.com/api/anomalyscores
 *
 *
 */
public class AnomalyScore extends AbstractResource {

    // Logging
    Logger logger = LoggerFactory.getLogger(AnomalyScore.class);

    /**
     * Constructor
     *
     */
    public AnomalyScore() {
    		super.init(null, null, false, null);
        this.resourceRe = ANOMALYSCORE_RE;
        this.resourceUrl = ANOMALYSCORE_URL;
        this.resourceName = "anomaly score";
    }

    /**
     * Constructor
     *
     */
    public AnomalyScore(final String apiUser, final String apiKey,
                        final boolean devMode) {
    		super.init(apiUser, apiKey, devMode, null);
        this.resourceRe = ANOMALYSCORE_RE;
        this.resourceUrl = ANOMALYSCORE_URL;
        this.resourceName = "anomaly score";
    }

    /**
     * Constructor
     *
     */
    public AnomalyScore(final String apiUser, final String apiKey,
                        final boolean devMode, final CacheManager cacheManager) {
    		super.init(apiUser, apiKey, devMode, cacheManager);
        this.resourceRe = ANOMALYSCORE_RE;
        this.resourceUrl = ANOMALYSCORE_URL;
        this.resourceName = "anomaly score";
    }

    /**
     * Creates a new anomaly score.
     *
     * POST
     * /andromeda/anomalyscore?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
     * HTTP/1.1 Host: bigml.io Content-Type: application/json
     *
     * @param anomalyId
     *            a unique identifier in the form anomaly/id where
     *            id is a string of 24 alpha-numeric chars for the anomaly
     *            to attach the anomaly score.
     * @param inputData
     *            an object with field's id/value pairs representing the
     *            instance you want to create an anomaly score for.
     * @param byName
     *            if we use the name of the fields instead of the internal code
     *            as the key to locate the fields in the inputData map.
     * @param args
     *            set of parameters for the new anomaly score. Required
     * @param waitTime
     *            time (milliseconds) to wait for next check of FINISHED status
     *            for anomaly before to start to create the anomaly score. Optional
     * @param retries
     *            number of times to try the operation. Optional
     *
     */
    @Deprecated
    public JSONObject create(final String anomalyId,
            JSONObject inputData, Boolean byName, String args,
            Integer waitTime, Integer retries) {
        JSONObject argsJSON = (JSONObject) JSONValue.parse(args);
        return create(anomalyId, inputData, byName, argsJSON, waitTime,
                retries);
    }

    /**
     * Creates a new anomaly score.
     *
     * POST
     * /andromeda/anomalyscore?username=$BIGML_USERNAME;api_key=$BIGML_API_KEY;
     * HTTP/1.1 Host: bigml.io Content-Type: application/json
     *
     * @param anomalyId
     *            a unique identifier in the form anomaly where
     *            id is a string of 24 alpha-numeric chars for the anomaly
     *            to attach the anomaly score.
     * @param inputData
     *            an object with field's id/value pairs representing the
     *            instance you want to create an anomaly score for.
     * @param byName
     *            if we use the name of the fields instead of the internal code
     *            as the key to locate the fields in the inputData map.
     * @param args
     *            set of parameters for the new anomaly score. Required
     * @param waitTime
     *            time (milliseconds) to wait for next check of FINISHED status
     *            for anomaly before to start to create the anomaly score. Optional
     * @param retries
     *            number of times to try the operation. Optional
     *
     */
    public JSONObject create(final String anomalyId,
            JSONObject inputData, Boolean byName, JSONObject args,
            Integer waitTime, Integer retries) {
        JSONObject anomaly = null;

        waitTime = waitTime != null ? waitTime : 3000;
        retries = retries != null ? retries : 10;

        try {
            anomaly = BigMLClient.getInstance(this.devMode).getAnomaly(
                    anomalyId);
            if (waitTime > 0) {
                int count = 0;
                while (count < retries
                        && !BigMLClient.getInstance(this.devMode)
                                .anomalyIsReady(anomaly)) {
                    Thread.sleep(waitTime);
                    count++;
                }
            }
        } catch (Throwable e) {
        }


        try {

            // Input data
            JSONObject inputDataJSON = null;
            if (inputData == null) {
                inputDataJSON = new JSONObject();
            } else {
                if (byName) {
                    JSONObject fields = (JSONObject) Utils.getJSONObject(anomaly,
                            "object.model.fields");

                    JSONObject invertedFields = Utils.invertDictionary(fields);
                    inputDataJSON = new JSONObject();
                    Iterator iter = inputData.keySet().iterator();
                    while (iter.hasNext()) {
                        String key = (String) iter.next();
                        if (invertedFields.get(key) != null) {
                            inputDataJSON.put( ((JSONObject) invertedFields.get(key)).get("fieldID"), inputData.get(key));
                        }
                    }
                } else {
                    inputDataJSON = inputData;
                }
            }

            JSONObject requestObject = new JSONObject();
            if (args != null) {
                requestObject = args;
            }

            requestObject.put("anomaly", anomalyId);
            requestObject.put("input_data", inputDataJSON);

            return createResource(ANOMALYSCORE_URL, requestObject.toJSONString());
        } catch (Throwable e) {
            logger.error("Error creating anomaly score", e);
            return null;
        }

    }

}
