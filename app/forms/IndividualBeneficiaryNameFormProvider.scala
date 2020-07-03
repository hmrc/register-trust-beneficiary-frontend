/*
 * Copyright 2020 HM Revenue & Customs
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

package forms

import forms.mappings.Mappings
import javax.inject.Inject
import models.core.pages.FullName
import play.api.data.Form
import play.api.data.Forms._

class IndividualBeneficiaryNameFormProvider @Inject() extends Mappings {

   def apply(): Form[FullName] =   Form(
    mapping(
      "firstName" -> text(s"individualBeneficiaryName.error.firstname.required")
        .verifying(
          firstError(
            maxLength(35, s"individualBeneficiaryName.error.firstname.length"),
            isNotEmpty("firstName", s"individualBeneficiaryName.error.firstname.required"),
            regexp(Validation.nameRegex, s"individualBeneficiaryName.error.firstname.invalid")
          )
        ),
      "middleName" -> optional(text()
        .verifying(
          firstError(
            maxLength(35, s"individualBeneficiaryName.error.middlename.length"),
            regexp(Validation.nameRegex, s"individualBeneficiaryName.error.middlename.invalid"))
        )
      ),
      "lastName" -> text(s"individualBeneficiaryName.error.lastname.required")
        .verifying(
          firstError(
            maxLength(35, s"individualBeneficiaryName.error.lastname.length"),
            isNotEmpty("lastName", s"individualBeneficiaryName.error.lastname.required"),
            regexp(Validation.nameRegex, s"individualBeneficiaryName.error.lastname.invalid")
          )
        )
    )(FullName.apply)(FullName.unapply)
   )
 }
