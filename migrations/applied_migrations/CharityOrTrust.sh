#!/bin/bash

echo ""
echo "Applying migration CharityOrTrust"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /charityOrTrust                        controllers.CharityOrTrustController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /charityOrTrust                        controllers.CharityOrTrustController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeCharityOrTrust                  controllers.CharityOrTrustController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeCharityOrTrust                  controllers.CharityOrTrustController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "charityOrTrust.title = charityOrTrust" >> ../conf/messages.en
echo "charityOrTrust.heading = charityOrTrust" >> ../conf/messages.en
echo "charityOrTrust.checkYourAnswersLabel = charityOrTrust" >> ../conf/messages.en
echo "charityOrTrust.error.required = Select yes if charityOrTrust" >> ../conf/messages.en

echo "Adding to UserAnswersEntryGenerators"
awk '/trait UserAnswersEntryGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCharityOrTrustUserAnswersEntry: Arbitrary[(CharityOrTrustPage.type, JsValue)] =";\
    print "    Arbitrary {";\
    print "      for {";\
    print "        page  <- arbitrary[CharityOrTrustPage.type]";\
    print "        value <- arbitrary[Boolean].map(Json.toJson(_))";\
    print "      } yield (page, value)";\
    print "    }";\
    next }1' ../test/generators/UserAnswersEntryGenerators.scala > tmp && mv tmp ../test/generators/UserAnswersEntryGenerators.scala

echo "Adding to PageGenerators"
awk '/trait PageGenerators/ {\
    print;\
    print "";\
    print "  implicit lazy val arbitraryCharityOrTrustPage: Arbitrary[CharityOrTrustPage.type] =";\
    print "    Arbitrary(CharityOrTrustPage)";\
    next }1' ../test/generators/PageGenerators.scala > tmp && mv tmp ../test/generators/PageGenerators.scala

echo "Adding to UserAnswersGenerator"
awk '/val generators/ {\
    print;\
    print "    arbitrary[(CharityOrTrustPage.type, JsValue)] ::";\
    next }1' ../test/generators/UserAnswersGenerator.scala > tmp && mv tmp ../test/generators/UserAnswersGenerator.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def charityOrTrust: Option[AnswerRow] = userAnswers.get(CharityOrTrustPage) map {";\
     print "    x =>";\
     print "      AnswerRow(";\
     print "        HtmlFormat.escape(messages(\"charityOrTrust.checkYourAnswersLabel\")),";\
     print "        yesOrNo(x),";\
     print "        routes.CharityOrTrustController.onPageLoad(CheckMode).url";\
     print "      )"
     print "  }";\
     next }1' ../app/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/utils/CheckYourAnswersHelper.scala

echo "Migration CharityOrTrust completed"
