package com.proxy;

public class PerformanceMonitor {

    private static ThreadLocal<MethodPerformance> performanceRecord = new ThreadLocal<>();

    public static void begin(String method) {
        System.out.println("begin monitor...");
        MethodPerformance performance = new MethodPerformance(method);
        performanceRecord.set(performance);
    }

    public static void end() {
        System.out.println("end monitor...");
        performanceRecord.get().printPerformance();
    }
}
