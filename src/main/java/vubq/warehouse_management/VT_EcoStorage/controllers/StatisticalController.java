package vubq.warehouse_management.VT_EcoStorage.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import vubq.warehouse_management.VT_EcoStorage.services.StatisticalService;
import vubq.warehouse_management.VT_EcoStorage.utils.https.Response;

import java.util.Calendar;
import java.util.Date;

@RestController
@RequestMapping("/api/statistical")
@RequiredArgsConstructor
public class StatisticalController {

    private final StatisticalService statisticalService;

    @PreAuthorize("hasAuthority('ADMIN.SUPER') or hasAuthority('STATISTICAL.VIEW')")
    @GetMapping
    public Response getStatistical(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
            @RequestParam String warehouseId,
            @RequestParam String keyword,
            @RequestParam boolean onlyWithTransaction
    ) {
        Calendar startCal = Calendar.getInstance();
        startCal.setTime(startDate);
        startCal.set(Calendar.HOUR_OF_DAY, 0);
        startCal.set(Calendar.MINUTE, 0);
        startCal.set(Calendar.SECOND, 0);
        startCal.set(Calendar.MILLISECOND, 0);
        Date start = startCal.getTime();

        Calendar endCal = Calendar.getInstance();
        endCal.setTime(endDate);
        endCal.set(Calendar.HOUR_OF_DAY, 23);
        endCal.set(Calendar.MINUTE, 59);
        endCal.set(Calendar.SECOND, 59);
        endCal.set(Calendar.MILLISECOND, 999);
        Date end = endCal.getTime();
        return Response.success(
                statisticalService.getStatistical(start, end, warehouseId, keyword, onlyWithTransaction)
        );
    }

    @GetMapping("/reference-data")
    public Response getReferenceData() {
        return Response.success(
                statisticalService.getReferenceDataStatistical()
        );
    }
}
