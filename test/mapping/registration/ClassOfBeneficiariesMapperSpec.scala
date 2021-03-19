/*
 * Copyright 2021 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mapping.registration

import base.SpecBase
import generators.Generators
import mapping.Mapping
import models.UnidentifiedType
import models.registration.pages.WhatTypeOfBeneficiary
import org.scalatest.{MustMatchers, OptionValues}
import pages.register.beneficiaries.WhatTypeOfBeneficiaryPage
import pages.register.beneficiaries.classofbeneficiaries.ClassBeneficiaryDescriptionPage

class ClassOfBeneficiariesMapperSpec extends SpecBase with MustMatchers
  with OptionValues with Generators {

  val classOfBeneficiariesMapper: Mapping[List[UnidentifiedType]] = injector.instanceOf[ClassOfBeneficiariesMapper]

  "ClassOfBeneficiaries" when {

    "when user answers is empty" must {

      "must not be able to create a beneficiary of 'class of beneficiary' (UnidentifiedType)" in {
        val userAnswers = emptyUserAnswers
        classOfBeneficiariesMapper.build(userAnswers) mustNot be(defined)
      }
    }

    "when user answers is not empty" must {

      "must be able to create a beneficiary of 'class of beneficiary' (UnidentifiedType)" in {
        val index = 0
        val userAnswers =
          emptyUserAnswers
            .set(ClassBeneficiaryDescriptionPage(index), "class of ben").success.value

        classOfBeneficiariesMapper.build(userAnswers).value.head mustBe UnidentifiedType(
            description = "class of ben",
            beneficiaryDiscretion = None,
            beneficiaryShareOfIncome = None)
      }

      "must be able to list of 'class of beneficiary' (UnidentifiedType)" in {
        val index0 = 0
        val index1 = 1

        val userAnswers =
          emptyUserAnswers
            .set(ClassBeneficiaryDescriptionPage(index0), "class of ben 1").success.value
            .set(ClassBeneficiaryDescriptionPage(index1), "class of ben 2").success.value

        classOfBeneficiariesMapper.build(userAnswers).value mustBe List(UnidentifiedType(
          description = "class of ben 1",
          beneficiaryDiscretion = None,
          beneficiaryShareOfIncome = None),
          UnidentifiedType(
          description = "class of ben 2",
          beneficiaryDiscretion = None,
          beneficiaryShareOfIncome = None)
        )
      }

      "must not able to create a beneficiary of 'class of beneficiary' (UnidentifiedType) when only this type of beneficiary does not exist" in {

        val userAnswers =
          emptyUserAnswers
            .set(WhatTypeOfBeneficiaryPage, WhatTypeOfBeneficiary.Individual).success.value

        classOfBeneficiariesMapper.build(userAnswers) mustBe None

      }


    }

  }

}
