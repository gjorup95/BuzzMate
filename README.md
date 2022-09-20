<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>




<!-- PROJECT SHIELDS -->
<!--
*** I'm using markdown "reference style" links for readability.
*** Reference links are enclosed in brackets [ ] instead of parentheses ( ).
*** See the bottom of this document for the declaration of the reference variables
*** for contributors-url, forks-url, etc. This is an optional, concise syntax you may use.
*** https://www.markdownguide.org/basic-syntax/#reference-style-links
-->



[![LinkedIn][linkedin-shield]][linkedin-url]



<h3 align="center">BuzzMate</h3>

  <p align="center">
   A native android project that uses GPS location to track the arrival of public transportation vehicles<br/>
  It does this to alert the visually impaired user of when to prepare to board and when to get off.
    <br />
   <div align="center">
  <img src="https://github.com/gjorup95/BuzzMate/blob/master/images/buzzmate%20cover.png" />
</div>
</div>



<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
      </ul>
    </li>
      </ul>
    </li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>



<!-- ABOUT THE PROJECT -->
## About The Project
BuzzMate is a combination of a physical shapechanging phone cover and a android application that allows a visually impaired user to track the movement of a public transportation vehicle in relation to the user.  <br />
In doing so it allows the user to be more conscious of a given trip without having access to the visual modality. By making sure the user is aware when to get off at the correct stopping point and when to board. <br>
The logic of the system is comprised of a firestore database that holds the updating GPS location of various public transport vehicles and the user. The application then continously polls this database to give feedback of the updating locations and provides this feedback through a phone cover that has shapechanging features. <br/>
These features are non-invasive and provides varying amounts of feedback depending on the urgency of the time frame. So that the invasiveness of the physical shapechange increases as the time frame shrinks.
<div align="center">
  <img src="https://github.com/gjorup95/BuzzMate/blob/master/images/cover.png" />
</div>
Simple architectural drawing of how data is fetched, stored and used from the firestore database to the physical manifestation in the phone cover.
<div align="center">
  <img src="https://github.com/gjorup95/BuzzMate/blob/master/images/arkitektur.png" />
</div>

<p align="right">(<a href="#readme-top">back to top</a>)</p>


<!-- CONTACT -->
## Contact

Project Link: [https://github.com/gjorup95/BuzzMate](https://github.com/gjorup95/BuzzMate)

<p align="right">(<a href="#readme-top">back to top</a>)</p>




[license-shield]: https://img.shields.io/github/license/github_username/repo_name.svg?style=for-the-badge
[license-url]: https://github.com/github_username/repo_name/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://www.linkedin.com/in/troels-hune-gj%C3%B8rup-88566410b/
[product-screenshot]: images/Hotciv.png
