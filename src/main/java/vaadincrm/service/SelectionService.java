package vaadincrm.service;

import com.vaadin.data.Property;
import com.vaadin.ui.NativeSelect;
import io.crm.QC;
import io.vertx.core.json.JsonObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by someone on 30/08/2015.
 */
public class SelectionService {

    private boolean ignoreSelectionChange = false;
    private Map<Long, JsonObject> houseMap;
    private Map<Long, JsonObject> areaMap;

    public SelectionService() {
    }

    public SelectionService(Map<Long, JsonObject> dataMap, Map<Long, JsonObject> areaMap) {
        this.houseMap = dataMap;
        this.areaMap = areaMap;
    }

    public SelectionService ignoreSelectionChange(boolean ignoreSelectionChange) {
        this.ignoreSelectionChange = ignoreSelectionChange;
        return this;
    }

    public SelectionService houseMap(HashMap<Long, JsonObject> dataMap) {
        this.houseMap = dataMap;
        return this;
    }

    public SelectionService areaMap(HashMap<Long, JsonObject> areaMap) {
        this.areaMap = areaMap;
        return this;
    }

    public void onAreaRegionSelection(final NativeSelect areaSelect, final NativeSelect regionSelect) {
        final Long Zero = 0L;
        regionSelect.addValueChangeListener(event -> {
            if (!ignoreSelectionChange) {
                try {
                    ignoreSelectionChange = true;
                    final Object selectedRegionId = event.getProperty().getValue();
                    Collection<JsonObject> areas = selectedRegionId.equals(0L) ? areaMap.values() : areaMap.values().stream().filter(j -> j.getJsonObject(QC.region, new JsonObject()).getLong(QC.id, 0L).equals(selectedRegionId)).collect(Collectors.toSet());
                    areaSelect.clear();
                    areaSelect.removeAllItems();
                    areaSelect.addItem(Zero);
                    areaSelect.setItemCaption(Zero, "Select Area");
                    areas.forEach(c -> {
                        final Long aId = c.getLong(QC.id);
                        areaSelect.addItem(aId);
                        areaSelect.setItemCaption(aId, c.getString(QC.name));
                    });
                    areaSelect.setValue(Zero);
                } finally {
                    ignoreSelectionChange = false;
                }
            }
        });

        areaSelect.addValueChangeListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(Property.ValueChangeEvent e) {
                if (!ignoreSelectionChange) {
                    try {
                        ignoreSelectionChange = true;
                        final Object selectedAreaId = e.getProperty().getValue();
                        final Long regionId = selectedAreaId.equals(0L) ? 0L : areaMap.get(selectedAreaId).getJsonObject(QC.region, new JsonObject()).getLong(QC.id, 0L);
                        regionSelect.setValue(regionId);
                    } finally {
                        ignoreSelectionChange = false;
                    }
                }
            }
        });
    }

    public void onAreaRegionSelection(NativeSelect houseSelect, NativeSelect areaSelect, NativeSelect regionSelect) {

        regionSelect.addValueChangeListener(event -> {
            if (!ignoreSelectionChange) {
                try {
                    ignoreSelectionChange = true;

                    final Object selectedRegionId = event.getProperty().getValue();
                    refillSelect(areaSelect, selectedRegionId);

                    Collection<JsonObject> houses = selectedRegionId.equals(0L) ? houseMap.values() : houseMap.values().stream()
                            .filter(j -> j.getJsonObject(QC.area, new JsonObject())
                                    .getJsonObject(QC.region, new JsonObject())
                                    .getLong(QC.id, 0L).equals(selectedRegionId)).collect(Collectors.toSet());

                    refillSelect2(houseSelect, houses);

                } finally {
                    ignoreSelectionChange = false;
                }
            }
        });

        areaSelect.addValueChangeListener(e -> {
            if (!ignoreSelectionChange) {
                try {
                    ignoreSelectionChange = true;
                    final Object selectedAreaId = e.getProperty().getValue();

                    final Long regionId = selectedAreaId.equals(0L) ? 0L : areaMap.get(selectedAreaId).getJsonObject(QC.region, new JsonObject()).getLong(QC.id, 0L);
                    regionSelect.setValue(regionId);

                    Collection<JsonObject> houses = selectedAreaId.equals(0L) ? houseMap.values() : houseMap.values().stream()
                            .filter(j -> j.getJsonObject(QC.area, new JsonObject())
                                    .getLong(QC.id, 0L).equals(selectedAreaId)).collect(Collectors.toSet());

                    refillSelect2(houseSelect, houses);

                } finally {
                    ignoreSelectionChange = false;
                }
            }
        });


        houseSelect.addValueChangeListener(e -> {
            if (!ignoreSelectionChange) {
                try {
                    ignoreSelectionChange = true;

                    final Object houseId = e.getProperty().getValue();

                    final Long areaId = houseId.equals(0L) ? 0L : houseMap.get(houseId).getJsonObject(QC.area, new JsonObject()).getLong(QC.id, 0L);
                    areaSelect.setValue(areaId);

                    final Long regionId = houseId.equals(0L) ? 0L : houseMap.get(houseId)
                            .getJsonObject(QC.area, new JsonObject())
                            .getJsonObject(QC.region, new JsonObject())
                            .getLong(QC.id, 0L);
                    regionSelect.setValue(regionId);

                } finally {
                    ignoreSelectionChange = false;
                }
            }
        });
    }

    private void refillSelect(NativeSelect areaSelect, Object selectedRegionId) {
        Collection<JsonObject> areas = selectedRegionId.equals(0L) ? areaMap.values() : areaMap.values().stream().filter(j -> j.getJsonObject(QC.region, new JsonObject()).getLong(QC.id, 0L).equals(selectedRegionId)).collect(Collectors.toSet());
        refillSelect2(areaSelect, areas, "Select Area");
    }

    private void refillSelect2(final NativeSelect houseSelect, final Collection<JsonObject> houses) {
        refillSelect2(houseSelect, houses, "Select House");
    }

    private void refillSelect2(final NativeSelect houseSelect, final Collection<JsonObject> houses, String caption) {
        final Long Zero = 0L;
        houseSelect.clear();
        houseSelect.removeAllItems();
        houseSelect.addItem(Zero);
        houseSelect.setItemCaption(Zero, caption);
        houses.forEach(c -> {
            final Long aId = c.getLong(QC.id);
            houseSelect.addItem(aId);
            houseSelect.setItemCaption(aId, c.getString(QC.name));
        });
        houseSelect.setValue(Zero);
    }

    public boolean isIgnoreSelectionChange() {
        return ignoreSelectionChange;
    }

    public Map<Long, JsonObject> getHouseMap() {
        return houseMap;
    }

    public Map<Long, JsonObject> getAreaMap() {
        return areaMap;
    }
}
