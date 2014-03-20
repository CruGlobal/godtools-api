INSERT INTO packages(id, name, code, default_language_id) VALUES('216c4ce3-e0a0-4192-ac87-e460a1121f20', 'Knowing God Personally', 'kgp', '5d469b6c-df1d-417a-a320-64039c2a898a');

INSERT INTO translations(id, package_id, language_id) VALUES('a1ace50b-68f3-41cb-b903-02f846daf0e4', '216c4ce3-e0a0-4192-ac87-e460a1121f20', '5d469b6c-df1d-417a-a320-64039c2a898a');

INSERT INTO versions(id, version_number, released, package_id, translation_id, minimum_interpreter_version) VALUES('89bedc8b-ae85-461e-95d6-cb65191966aa', 1, true, '216c4ce3-e0a0-4192-ac87-e460a1121f20', 'a1ace50b-68f3-41cb-b903-02f846daf0e4', 1);

UPDATE versions SET package_structure = '<document lang="en">
<packagename translate="true">Knowing God Personally</packagename>
<page thumb="PageThumb_01.png" filename="01_Home.xml" translate="true">Home</page>
<page thumb="PageThumb_02.png" filename="02_FirstPoint.xml" translate="true">1 God Loves You And Created You To Know Him Personally.</page>
<page thumb="PageThumb_03.png" filename="03_SecondPoint.xml" translate="true">2 We Are Separated From God By Our Sin, So We Cannot Know Him Or Experience His Love.</page>
<page thumb="PageThumb_04.png" filename="04_ThirdPoint.xml" translate="true">3 Jesus Is God''s Only Solution For Our Sin. Only Through Him Can We Know God And Receive His Love And Forgiveness.</page>
<page thumb="PageThumb_05.png" filename="05_FourthPoint.xml" translate="true">4 We Must Each Respond To Jesus By Placing Our Trust In Him As Our Saviour And Lord. Only Then Can We Know God Personally.</page>
<page thumb="PageThumb_06.png" filename="06_TwoCircles.xml" translate="true">These Circles Describe Two Types Of People</page>
<page thumb="PageThumb_07.png" filename="07_AttitudeOfYourHeart.xml" translate="true">To Begin A Relationship With God, You Must Give Jesus Everything.</page>
<page thumb="PageThumb_08.png" filename="08_SuggestedPrayer.xml" translate="true">You Can Express Your Attitude Toward God Through Prayer.</page>
<page thumb="PageThumb_09.png" filename="09_WhatHappens.xml" translate="true">What Happens When You Put Your Trust In Jesus?</page>
<page thumb="PageThumb_10.png" filename="10_WhatHappensTwo.xml" translate="true">If You Asked Jesus Into Your Life As Saviour And Lord, Many Things Have Happened, Including:</page>
<page thumb="PageThumb_11.png" filename="11_HowCanYouBeSure.xml" translate="true">How Can You Be Sure That All This Has Really Happened?</page>
<page thumb="PageThumb_12.png" filename="12_HowToGrow.xml" translate="true">How To Grow As A Follower Of Jesus</page>
<page thumb="PageThumb_13.png" filename="13_FinalPage.xml" translate="true">Websites To Assist You</page>
<about filename="00_About.xml" translate="true">About</about>
    <instructions translate="true"></instructions>
</document>' WHERE id = '89bedc8b-ae85-461e-95d6-cb65191966aa';

INSERT INTO pages(id, version_id, ordinal, description, xml_content) VALUES('9267317f-1ca4-4206-97a8-635ca25be1a1', '89bedc8b-ae85-461e-95d6-cb65191966aa', 1, '1 God Loves You And Created You To Know Him Personally.','<page backgroundimage="grass.png" color="#7EB14F">
<title mode="clear">
<heading color="#000000" textalign="left" size="200" x="19" y="25" w="300" translate="true">KNOWING GOD</heading>
<subheading color="#000000" textalign="right" size="211" x="0" y="60" w="275" translate="true">personally</subheading>
</title>

<text color="#000000" modifier="italics" size="112" xoffset="19" yoffset="40" w="260" translate="true">These four points explain how to enter into a personal relationship with God and experience the life for which you were created.</text>

</page>');