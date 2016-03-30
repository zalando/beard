package de.zalando.beard.filter

import de.zalando.beard.filter.implementations.DateFormatFilter
import org.scalatest.{FunSpec, Matchers}

/**
  * Created by scherniavsky on 3/26/16.
  */
class DateFormatFilterTest extends FunSpec with Matchers {
  describe("DateFormat filter test") {
    val filter = DateFormatFilter()
    it("should recognize epoch: 981169506") {
      filter.apply("981169506", Map("format" -> "d-M-yy H:m:s.SSS")) shouldBe "3-2-01 4:5:6.000"
    }
    it("should recognize millis: 981169506987") {
      filter.apply("981169506987", Map("format" -> "dd-MM-yyyy HH:mm:ss.SSS")) shouldBe "03-02-2001 04:05:06.987"
    }
    it("should recognize yyyyMMdd: 20010203") {
      filter.apply("20010203", Map("format" -> "dd-MM-yyyy")) shouldBe "03-02-2001"
    }
    it("should recognize yyyy-MM-dd HH:mm:ss: 2001-02-03 04:05:06") {
      filter.apply("2001-02-03 04:05:06", Map("format" -> "dd-MM-yyyy HH:mm:ss")) shouldBe "03-02-2001 04:05:06"
    }
    it("should recognize yyyy-MM-dd HH:mm:ss: 2001-02-03 04:05:06+03:00") {
      filter.apply("2001-02-03 04:05:06", Map("format" -> "dd-MM-yyyy HH:mm:ss")) shouldBe "03-02-2001 04:05:06"
    }
    it("should recognize yyyy-MM-dd: 2001-02-03") {
      filter.apply("2001-02-03", Map("format" -> "dd-MM-yyyy")) shouldBe "03-02-2001"
    }
    it("should recognize ISO_LOCAL_DATE_TIME: 2001-02-03T04:05:06") {
      filter.apply("2001-02-03T04:05:06", Map("format" -> "dd-MM-yyyy HH:mm:ss")) shouldBe "03-02-2001 04:05:06"
    }
    it("should recognize ISO_OFFSET_DATE_TIME: 2001-02-03T04:05:06+01:00") {
      filter.apply("2001-02-03T04:05:06+01:00", Map("format" -> "dd-MM-yyyy HH:mm:ss")) shouldBe "03-02-2001 04:05:06"
    }
    it("should recognize ISO_INSTANT: 2001-02-03T04:05:06.789Z") {
      filter.apply("2001-02-03T04:05:06.789Z", Map("format" -> "MM-yyyy mm:ss.SSS")) shouldBe "02-2001 05:06.789"
    }
    it("should recognize ISO_INSTANT: 2001-02-03T04:05:06Z") {
      filter.apply("2001-02-03T04:05:06Z", Map("format" -> "MM-yyyy mm:ss")) shouldBe "02-2001 05:06"
    }
    it("should recognize yyyy-M-dd: 2001-2-03") {
      filter.apply("2001-2-13", Map("format" -> "dd-MM-yyyy")) shouldBe "13-02-2001"
    }
    it("should recognize yyyy-MM-d: 2001-12-3") {
      filter.apply("2001-12-3", Map("format" -> "dd-MM-yyyy")) shouldBe "03-12-2001"
    }
    it("should recognize yyyy-M-d: 2001-2-3") {
      filter.apply("2001-2-3", Map("format" -> "dd-MM-yyyy")) shouldBe "03-02-2001"
    }
    it("should recognize dd-MM-yyyy: 03-02-2001") {
      filter.apply("03-02-2001", Map("format" -> "yyyy-MM-dd")) shouldBe "2001-02-03"
    }
    it("should recognize dd-MM-yyyy HH:mm:ss: 03-02-2001 04:05:06") {
      filter.apply("03-02-2001 04:05:06", Map("format" -> "yyyy-MM-dd ss:mm:HH")) shouldBe "2001-02-03 06:05:04"
    }
    it("should recognize d-MM-yyyy: 3-12-2001") {
      filter.apply("3-12-2001", Map("format" -> "yyyy-MM-dd")) shouldBe "2001-12-03"
    }
    it("should recognize dd-M-yyyy: 13-2-2001") {
      filter.apply("13-2-2001", Map("format" -> "yyyy-MM-dd")) shouldBe "2001-02-13"
    }
    it("should recognize d-M-yyyy: 3-2-2001") {
      filter.apply("3-2-2001", Map("format" -> "yyyy-MM-dd")) shouldBe "2001-02-03"
    }
    it("should recognize HH:mm:ss: 04:05:06") {
      filter.apply("04:05:06", Map("format" -> "ss:mm:HH")) shouldBe "06:05:04"
    }
  }
}
