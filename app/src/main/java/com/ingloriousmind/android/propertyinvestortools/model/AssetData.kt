package com.ingloriousmind.android.propertyinvestortools.model

data class AssetData(var acquisitionCost: Float = 0f, var purchaseRelatedCosts: Float = 0f, var annualNetRent: Float = 0f, var annualRunningCosts: Float = 0f) {

    fun extendedAcquisitionCost(): Float = acquisitionCost * (100 + purchaseRelatedCosts) / 100

    fun adjustedAnnualNetRent(): Float = annualNetRent - annualRunningCosts

    fun `yield`(): Float = if (extendedAcquisitionCost() == 0f) 0f else 100 * adjustedAnnualNetRent().div(extendedAcquisitionCost())

    fun multiplier(): Float = if (adjustedAnnualNetRent() == 0f) 0f else extendedAcquisitionCost() / adjustedAnnualNetRent()

}