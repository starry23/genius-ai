package cn.apeto.geniusai.server.strategy;

import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gblhjh
 * @Date: 2023/07/31/14:58
 * @Description: initKey(List<String> keys)默认初始化key,默认权重一样使用的是轮询
 * *             initKey(List<String> keys,int[] weights)添加平滑权重 每个key的顺序是固定的,5key和120key一起可以适当设置权重
 */
@Slf4j
public class SmoothWeightedRoundRobinStrategy implements RobinWeightStrategy {
    public static void main(String[] args) {
        RobinWeightStrategy strategy = new SmoothWeightedRoundRobinStrategy();
        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");
//        int[] nums = new int[]{1,1,1,1,1};
        strategy.initKey(list);
        for (int i = 0; i < 22; i++) {
            String strategy1 = strategy.strategy();
            System.out.println(strategy1);
        }
        List<String> list2 = new ArrayList<>();
        list2.add("6");
        list2.add("7");
        strategy.initKey(list2);
        System.out.println("分割线----------------------");
        for (int i = 0; i < 20; i++) {
            String strategy1 = strategy.strategy();
            System.out.println(strategy1);
        }
    }

    // 密钥和对应的权重值
    private Map<String, Integer> keyWeights;
    // 权重的初始值
    private Map<String, Integer> currentWeights;

    public SmoothWeightedRoundRobinStrategy() {
        keyWeights = new HashMap<>();
        currentWeights = new HashMap<>();
    }

    @Override
    public String strategy() {
        // 获取所有密钥
        List<String> keys = getKeys();

        // 检查权重是否为空
        if (keyWeights.isEmpty()) {
            log.error("-------------> 负载均衡校验出错");
//            throw new BaseException(CommonError.KEY_SMOOTH_ERROR);
        }

        // 选择权重最大的密钥
        String selectedKey = null;
        int maxWeight = Integer.MIN_VALUE;

        for (String key : keys) {
            // 获取key的权重
            int weight = keyWeights.get(key);
            // 获取权重初始值 权重值 = 初始值 + key权重
            int currentWeight = currentWeights.getOrDefault(key, 0) + weight;

            currentWeights.put(key, currentWeight);

            if (selectedKey == null || currentWeight > maxWeight) {
                selectedKey = key;
                maxWeight = currentWeight;
            }
        }

        // currentWeights[i] = currentWeights[i] - totalWeight
        currentWeights.put(selectedKey, currentWeights.get(selectedKey) - totalWeight());

        return selectedKey;
    }

    @Override
    public List<String> getKeys() {
        return new ArrayList<>(LOCAL_KEYS);
    }

    @Override
    public KeyStrategy initKey(List<String> keys) {
        if (!Objects.nonNull(keys)) {
            log.error("-------------> 负载均衡校验出错");
//            throw new BaseException(CommonError.KEY_SMOOTH_ERROR);
        }

        // 调用父类初始化
        RobinWeightStrategy.super.initKey(keys);
        initializeWeights();
        return this;
    }
    /**
     * * 重写初始化key
     * @param keys
     * @param weights   权重
     * @return
     */
    @Override
    public KeyStrategy initKey(List<String> keys,int[] weights) {
        // 校验
        if (!Objects.nonNull(keys) && Objects.nonNull(weights) && weights.length != keys.size()) {
            log.error("-------------> 负载均衡校验出错");
//            throw new BaseException(CommonError.KEY_SMOOTH_ERROR);
        }

        // 调用父类初始化
        RobinWeightStrategy.super.initKey(keys);
        initializeWeights(weights);
        return this;
    }

    @Override
    public void removeErrorKey(String key) {
        RobinWeightStrategy.super.removeErrorKey(key);
        keyWeights.remove(key);
        currentWeights.remove(key);
    }

    @Override
    public void keysWarring() {
        if (keyWeights.isEmpty()) {
            System.out.println("All keys are invalid!");
        }
    }

    // 初始化权重值和当前权重
    private void initializeWeights() {
        keyWeights.clear();
        currentWeights.clear();
        List<String> keys = getKeys();
        int weight = keys.size();
        for (String key : keys) {
            keyWeights.put(key, weight);
            // current开始初始值为0
            currentWeights.put(key,0);
        }

    }

    // 初始化权重值
    private void initializeWeights(int[] weights) {
        keyWeights.clear();
        currentWeights.clear();
        List<String> keys = getKeys();
        for (int i = 0; i < weights.length; i++) {
            keyWeights.put(keys.get(i),weights[i]);
            currentWeights.put(keys.get(i),0);
        }
    }


    // 计算总权重
    private int totalWeight() {
        return keyWeights.values().stream().mapToInt(Integer::intValue).sum();
    }
}
