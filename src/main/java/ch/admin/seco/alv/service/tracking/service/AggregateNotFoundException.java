package ch.admin.seco.alv.service.tracking.service;


public abstract class AggregateNotFoundException extends RuntimeException {

    private final Class<?> clazz;

    private final String aggregateId;

    protected AggregateNotFoundException(Class<?> clazz, String aggregateId) {
        super("Aggregate: '" + clazz.getSimpleName() + "' having Id: '" + aggregateId + "' not found");
        this.clazz = clazz;
        this.aggregateId = aggregateId;
    }

    public String getAggregateId() {
        return aggregateId;
    }

    public String getAggregateType() {
        return clazz.getSimpleName();
    }
}
