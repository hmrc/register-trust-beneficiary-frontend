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

package config

import com.google.inject.AbstractModule
import config.annotations.{CharityBeneficiary, ClassOfBeneficiaries, CompanyBeneficiary, IndividualBeneficiary, TrustBeneficiary}
import controllers.actions.register._
import navigation.{BeneficiaryNavigator, CharityBeneficiaryNavigator, ClassOfBeneficiariesNavigator, CompanyBeneficiaryNavigator, IndividualBeneficiaryNavigator, Navigator, TrustBeneficiaryNavigator}
import repositories.{DefaultRegistrationsRepository, RegistrationsRepository}

class Module extends AbstractModule {

  override def configure(): Unit = {
    bind(classOf[RegistrationsRepository]).to(classOf[DefaultRegistrationsRepository]).asEagerSingleton()
    bind(classOf[RegistrationDataRequiredAction]).to(classOf[RegistrationDataRequiredActionImpl]).asEagerSingleton()
    bind(classOf[DraftIdRetrievalActionProvider]).to(classOf[DraftIdDataRetrievalActionProviderImpl]).asEagerSingleton()

    bind(classOf[Navigator]).annotatedWith(classOf[CompanyBeneficiary]).to(classOf[CompanyBeneficiaryNavigator]).asEagerSingleton()
    bind(classOf[Navigator]).annotatedWith(classOf[TrustBeneficiary]).to(classOf[TrustBeneficiaryNavigator]).asEagerSingleton()
    bind(classOf[Navigator]).annotatedWith(classOf[IndividualBeneficiary]).to(classOf[IndividualBeneficiaryNavigator]).asEagerSingleton()
    bind(classOf[Navigator]).annotatedWith(classOf[ClassOfBeneficiaries]).to(classOf[ClassOfBeneficiariesNavigator]).asEagerSingleton()
    bind(classOf[Navigator]).annotatedWith(classOf[CharityBeneficiary]).to(classOf[CharityBeneficiaryNavigator]).asEagerSingleton()
    bind(classOf[Navigator]).to(classOf[BeneficiaryNavigator]).asEagerSingleton()
  }
}
