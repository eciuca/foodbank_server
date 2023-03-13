<?php
// cette fonction calcule l'age en jours et tient compte des années bisextiles.
function AgeJours($jour, $mois, $annee) {
    $now = time();
    $your_date = strtotime($jour.'-'.$mois.'-'.$annee);
    $datediff = $now - $your_date;

    return floor($datediff / (60 * 60 * 24));
}

// Ce code ventile chaque jour les bénéficiaires /organisation / tranche d'âge et met à jour les populations des organisations;// La seconde partie du code évalue et met à jour les suspensions d'organisation.

echo "Starting population.php at " . date("Y-m-d H:i:s") . "\n";
$errormsg = "";
while ( true) {
$host= getenv('MYSQL_HOST');
$user =getenv('MYSQL_USER');
$password =getenv('MYSQL_PASSWORD');
$database =getenv('MYSQL_DATABASE');

$connection= mysqli_connect($host,$user,"$password",$database);

if (mysqli_connect_errno())
{
    $errormsg=  "Failed to connect to MySQL: " . mysqli_connect_error();
    break;
}



$now = time();
$mydate=date("Y-m-d",$now);

$prog='';
$fixinf="update dep set eq = 1 where eq = 0";
$resfixinf = mysqli_query($connection,$fixinf);
$reqdis ="SELECT id_dis, nbrefix,lien_depot,lien_banque,msonac,gest_ben,n_pers,n_eq,n_fam,n_nour,n_bebe,n_enf,n_ado,n_18_24,n_sen FROM organisations WHERE 0=0  and actif = 1 and depy_n = 0  ORDER BY societe";
$resdis= mysqli_query($connection,$reqdis);
while ($datadis= mysqli_fetch_object($resdis)) // while 1
{
    set_time_limit(120);

    $m_ac=$datadis->msonac;
    $gm=$datadis->gest_ben;// = 1 si la gestion des bénéficiaires est faites sur le site
    $lb=$datadis->lien_banque;
    $ld=$datadis->lien_depot;
    $nfix=$datadis->nbrefix; // nombre fixe pour les maisons d'accueil - nbre de lits agréés
    $pers=$datadis->n_pers;
    $eq=$datadis->n_eq;
    $actif=$datadis->n_fam;
    $nourisson=$datadis->n_nour;
    $bebe=$datadis->n_bebe; // bebe/assoc
    $enf=$datadis->n_enf; // enfants /assoc
    $ad=$datadis->n_ado;//ados / association
    $youngad=$datadis->n_18_24; // jeunes adultes 18-24 ans
    $senior=$datadis->n_sen; //+ 65 ans
    if($m_ac == 0 && $gm == 0) //pas maison d'accueil et pas de gestion des bénéficiaires sur le site
        $prog = 1;
    if($m_ac == 0 && $gm == 1) //pas maison d'accueil et gestion des bénéficiaires sur le site
        $prog = 2;
    if($m_ac == 1 && $gm == 1) // maison d'accueil et  gestion des bénéficiaires supplémentaires sur le site
        $prog = 3;
    if($m_ac == 1 && $gm == 0) //maison d'accueil et pas de gestion des bénéficiaires sur le site
        $prog = 4;
    switch ($prog)
    {
        case 1: //pas maison d'accueil et pas de gestion des bénéficiaires sur le site
            $countactif=$actif;
            $countmember=$pers; // personnes
            $counteq=$pers; // equivalents
            $countnourisson=$nourisson;
            $countbebe=$bebe; // bebe/assoc
            $countenf=$enf; // enfants /assoc
            $countad=$ad;//ados / association
            $count18_24=$youngad;//jeunes adultes / assoc
            $countsenior=$senior; //+ 65 ans
            if($countnourisson == '') {
                $countnourisson = 0;
            }
            if($countmember == '') {
                $countmember = 0;
            }
            if($countactif == '') {
                $countactif = 0;
            }
            if($countbebe == '') {
                $countbebe = 0;
            }
            if($countsenior == '') {
                $countsenior = 0;
            }
            if($countad == '') {
                $countad = 0;
            }
            if($count18_24 == '') {
                $count18_24 = 0;
            }
            if($countenf == '') {
                $countenf = 0;
            }

            $requete ="UPDATE organisations SET 
			n_fam='".$countactif."',
			n_pers='".$countmember."',
			n_nour='".$countnourisson."',
			n_bebe='".$countbebe."',
			n_enf='".$countenf."',
			n_ado='".$countad."',
			n_18_24='".$count18_24."',
			n_sen='".$countsenior."',
			n_eq='".number_format($pers, 2, '.', '')."' WHERE id_dis=".$datadis->id_dis."";

            $resultat=mysqli_query($connection,$requete);
            if (!$resultat)
            {
                echo $requete ;
                //echo '<br>Il y a une erreur dans le programme 1 de compte.php. Veuillez essayer plus tard'. mysqli_error($connection);
            }
            else
            {
                $requete ="INSERT INTO population (date_stat,id_dis,n_fam,n_pers,n_nour,n_bebe,n_enf,n_ado,n_18_24,n_eq,lien_dep,n_sen,lien_banque) VALUES('".$mydate."','".$datadis->id_dis."','".$countactif."','".$countmember."','".$countnourisson."','".$countbebe."','".$countenf."','".$countad."','".$count18_24."','".number_format($pers, 2, ',', ' ')."','".$ld."','".$countsenior."','".$lb."')";

                $resultat=mysqli_query($connection,$requete);
                if (!$resultat)
                {
                    echo $requete;
                    echo '<br>Il y a une erreur dans l\'insert 1 en population. Veuillez essayer plus tard'. mysqli_error($connection);
                }
                else
                {
                    //echo "Statistiques mises &agrave; jour pour insert 1.";
                }
            }

            break;

        case 2: //pas maison d'accueil et gestion des bénéficiaires sur le site
            $drapeau=0; // change lorsque id_dis change
            $countactif=0; // familles/assoc
            $countnourisson=0; // nour/assoc
            $countbebe=0; // bebe/assoc
            $countenf=0; // enfants /assoc
            $countad=0;//ados / association
            $count18_24=0;//jeunes adultes de 18 à 24 ans
            $countsenior=0; //+ 65 ans
            $countmember = 0;
            $counteq=0;
            $requete ="SELECT daten,daten_conj,id_client,coeff,nomconj FROM clients WHERE lien_dis=".$datadis->id_dis." and actif=1";
            $resultat= mysqli_query($connection,$requete);

            while ($data= mysqli_fetch_object($resultat)) // while 2
            {
                $drapeau=1;
                $lm = $data->id_client;
                $coef = $data->coeff;
                $countactif = $countactif +1;
                $countmember = $countmember +1;
                $counteq = $counteq +(1/$coef);

                if (!empty($data->nomconj)) // calcul de l'âge du conjoint
                {
                    $countmember = $countmember +1;
                    $counteq = $counteq +(1/$coef);
                    $datnaissance = explode("/",$data->daten_conj);
                    $age = AgeJours($datnaissance[0],$datnaissance[1],$datnaissance[2]);
                }

                if ($age>23741) {
                    $countsenior=$countsenior+1;
                }
                if ($age>6570 and $age<8760) {
                    $count18_24=$count18_24+1;
                }
                // calcul de l'âge du chef de famille
                $datnaissance = explode("/",$data->daten);
                $age = AgeJours($datnaissance[0],$datnaissance[1],$datnaissance[2]);
                if ($age>23741) {
                    $countsenior=$countsenior+1;
                }
                if ($age>6570 and $age<8760) {
                    $count18_24=$count18_24+1;
                }
                $query = "SELECT * FROM dep WHERE lien_mast= $lm and deleted = 0"; // calcul de l'âge de chaque dépendant
                $result =  mysqli_query($connection,$query);
                while ($data2= mysqli_fetch_object($result)) // while 3
                {
                    $coef=$data2->eq;
                    $datnaissance = explode("/",$data2->datenais);
                    $age = AgeJours($datnaissance[0],$datnaissance[1],$datnaissance[2]);// jour/mois/année = paramètres passés à la fonction Agejours dans fonction_age.php
                    if ($age <=182)
                    {
                        $countnourisson = $countnourisson + 1;
                        $countmember = $countmember + 1;
                        $counteq = $counteq +(1/$coef);
                    }
                    if ( $age >182 && $age<730)
                    {
                        $countbebe = $countbebe +1;
                        $countmember = $countmember + 1;
                        $counteq = $counteq +(1/$coef);
                    }
                    if ($age >=730 && $age<4745)
                    {
                        $countenf = $countenf + 1;
                        $countmember = $countmember + 1;
                        $counteq = $counteq +(1/$coef);
                    }

                    if ($age >=4745 && $age<6570)
                    {
                        $countad = $countad + 1;
                        $countmember = $countmember + 1;
                        $counteq = $counteq +(1/$coef);
                    }
                    if ($age>=6570 and $age<8760)
                    {
                        $countmember = $countmember + 1;
                        $counteq = $counteq +(1/$coef);
                        $count18_24=$count18_24+1;
                    }
                    if ($age>=8760 && $age<23741)
                    {
                        $countmember = $countmember + 1;
                        $counteq = $counteq +(1/$coef);
                        $countad =$countad + 1;
                    }

                    if ($age>=23741)
                    {
                        $countsenior = $countsenior + 1;
                        $countmember = $countmember + 1;
                        $counteq = $counteq +(1/$coef);
                    }
                }//end while 3
            }// end while 2

            if($countnourisson == '') {
                $countnourisson = 0;
            }
            if($countmember == '') {
                $countmember = 0;
            }
            if($countactif == '') {
                $countactif = 0;
            }
            if($countbebe == '') {
                $countbebe = 0;
            }
            if($countsenior == '') {
                $countsenior = 0;
            }
            if($countad == '') {
                $countad = 0;
            }
            if($count18_24 == '') {
                $count18_24 = 0;
            }
            if($countenf == '') {
                $countenf = 0;
            }

            $requete ="UPDATE organisations SET 
			n_fam='".$countactif."',
			n_pers='".$countmember."',
			n_nour='".$countnourisson."',
			n_bebe='".$countbebe."',
			n_enf='".$countenf."',
			n_ado='".$countad."',
			n_18_24='".$count18_24."',
			n_sen='".$countsenior."',
			n_eq='".number_format($counteq, 2, '.', '')."' WHERE id_dis=".$datadis->id_dis."";

            //echo $requete;	

            $resultat=mysqli_query($connection,$requete);
            if (!$resultat)
            {
                echo $requete ;
                echo '<br>Il y a une erreur dans le programme  2 compte.php. Veuillez essayer plus tard'. mysqli_error($connection);
            }
            else
            {
                $requete ="INSERT INTO population (date_stat,id_dis,n_fam,n_pers,n_nour,n_bebe,n_enf,n_ado,n_18_24,n_eq,lien_dep,n_sen,lien_banque) VALUES('".$mydate."','".$datadis->id_dis."','".$countactif."','".$countmember."','".$countnourisson."','".$countbebe."','".$countenf."','".$countad."','".$count18_24."','".number_format($counteq, 2, ',', ' ')."','".$ld."','".$countsenior."','".$lb."')";

                $resultat=mysqli_query($connection,$requete);
                if (!$resultat)
                {
                    echo $requete;
                    echo '<br>Il y a une erreur dans l\'insert 2 en population. Veuillez essayer plus tard'. mysqli_error($connection);
                }
                else
                {
                    echo "Statistiques mises &agrave; jour pour l'insert 2.";
                }
            }

            break;
        case 3: // maison d'accueil et  gestion des bénéficiaires supplémentaires sur le site
            $countactif=$nfix;
            $countnourisson=0;
            $countbebe=0; // bebe/assoc
            $countenf=0; // enfants /assoc
            $countad=0;//ados / association
            $count18_24=0;//jeunes adulres 18-24
            $countsenior=0; //+ 65 ans
            $countmember=$nfix; // ajout du nombre fixe 
            $counteq=$nfix; // ajout du nombre fixe 
            $requete ="SELECT * FROM clients WHERE lien_dis=".$datadis->id_dis." and actif=1";
            $resultat= mysqli_query($connection,$requete);

            while ($data= mysqli_fetch_object($resultat)) // while 4
            {
                $drapeau=1;
                $lm = $data->id_client;
                $coef = $data->coeff;
                $countactif = $countactif +1;
                $countmember = $countmember +1;
                $counteq = $counteq +(1/$coef);

                if (!empty($data->nomconj)) // calcul de l'âge du conjoint
                {
                    $countmember = $countmember +1;
                    $counteq = $counteq +(1/$coef);
                    $datnaissance = explode("/",$data->daten_conj);
                    $age = AgeJours($datnaissance[0],$datnaissance[1],$datnaissance[2]);
                }
                if ($age>6570 and $age<8760) {
                    $count18_24=$count18_24+1;
                }

                if ($age>23741) {
                    $countsenior=$countsenior+1;
                }

                // calcul de l'âge du chef de famille
                $datnaissance = explode("/",$data->daten);
                $age = AgeJours($datnaissance[0],$datnaissance[1],$datnaissance[2]);
                if ($age>23741) {
                    $countsenior=$countsenior+1;
                }

                $query = "SELECT * FROM dep WHERE lien_mast= $lm and actif = 1  and deleted = 0"; // calcul de l'âge de chaque dépendant
                $result =  mysqli_query($connection,$query);
                while ($data2= mysqli_fetch_object($result)) // while 5
                {
                    $coef=$data2->eq;
                    $datnaissance = explode("/",$data2->datenais);
                    $age = AgeJours($datnaissance[0],$datnaissance[1],$datnaissance[2]);

                    if ($age <=182)
                    {
                        $countnourisson = $countnourisson + 1;
                        $countmember = $countmember + 1;
                        $counteq = $counteq +(1/$coef);
                    }
                    if ( $age >182 && $age<730)
                    {
                        $countbebe = $countbebe +1;
                        $countmember = $countmember + 1;
                        $counteq = $counteq +(1/$coef);
                    }
                    if ($age >=730 && $age<4745)
                    {
                        $countenf = $countenf + 1;
                        $countmember = $countmember + 1;
                        $counteq = $counteq +(1/$coef);
                    }

                    if ($age >=4745 && $age<6570)
                    {
                        $countad = $countad + 1;
                        $countmember = $countmember + 1;
                        $counteq = $counteq +(1/$coef);
                    }
                    if ($age>=6570 and $age<8760)
                    {
                        $countmember = $countmember + 1;
                        $counteq = $counteq +(1/$coef);
                        $count18_24=$count18_24+1;
                    }
                    if ($age>=8760 && $age<23741)
                    {
                        $countmember = $countmember + 1;
                        $counteq = $counteq +(1/$coef);
                        $countad =$countad + 1;
                    }

                    if ($age>=23741)
                    {
                        $countsenior = $countsenior + 1;
                        $countmember = $countmember + 1;
                        $counteq = $counteq +(1/$coef);
                    }
                }// end while 5
            }// end while 4

            if($countnourisson == '') {
                $countnourisson = 0;
            }
            if($countmember == '') {
                $countmember = 0;
            }
            if($countactif == '') {
                $countactif = 0;
            }
            if($countbebe == '') {
                $countbebe = 0;
            }
            if($countsenior == '') {
                $countsenior = 0;
            }
            if($countad == '') {
                $countad = 0;
            }
            if($count18_24 == '') {
                $count18_24 = 0;
            }
            if($countenf == '') {
                $countenf = 0;
            }

            $requete ="UPDATE organisations SET 
			n_fam='".$countactif."',
			n_pers='".$countmember."',
			n_nour='".$countnourisson."',
			n_bebe='".$countbebe."',
			n_enf='".$countenf."',
			n_ado='".$countad."',
			n_18_24='".$count18_24."',
			n_sen='".$countsenior."',
			n_eq='".number_format($counteq, 2, '.', '')."' WHERE id_dis=".$datadis->id_dis."";

            $resultat=mysqli_query($connection,$requete);
            if (!$resultat)
            {
                echo $requete ;
                echo '<br>Il y a une erreur dans le programme 3 compte.php. Veuillez essayer plus tard'. mysqli_error($connection);
            }
            else
            {
                $requete ="INSERT INTO population (date_stat,id_dis,n_fam,n_pers,n_nour,n_bebe,n_enf,n_ado,n_18_24,n_eq,lien_dep,n_sen,lien_banque) VALUES('".$mydate."','".$datadis->id_dis."','".$countactif."','".$countmember."','".$countnourisson."','".$countbebe."','".$countenf."','".$countad."','".$count18_24."','".number_format($counteq, 2, ',', ' ')."','".$ld."','".$countsenior."','".$lb."')";

                $resultat=mysqli_query($connection,$requete);
                if (!$resultat)
                {
                    echo $requete;
                    echo '<br>Il y a une erreur dans l\'insert  3 en population. Veuillez essayer plus tard'. mysqli_error($connection);
                }
                else
                {
                    echo "Statistiques mises &agrave; jour pour l'insert 3.";
                }
            }

            break;
        case 4://maison d'accueil et pas de gestion des bénéficiaires sur le site
            //	echo 'enfants ='.$enf;
            $countactif=$nfix;
            $countmember=$nfix; // nbrefix = nombre de personnes agréées pour maison d'accueil (pas d'enregistrement individuel)
            $counteq=$nfix;
            $countnourisson=$nourisson;
            $countbebe=$bebe; // bebe/assoc
            $countenf=$enf; // enfants /assoc
            $countad=$ad;//ados / association
            $count18_24=$youngad; // jeunes adultes /assoc
            $countsenior=$senior; //+ 65 ans
            if($countnourisson == '') {
                $countnourisson = 0;
            }
            if($countmember == '') {
                $countmember = 0;
            }
            if($countactif == '') {
                $countactif = 0;
            }
            if($countbebe == '') {
                $countbebe = 0;
            }
            if($countsenior == '') {
                $countsenior = 0;
            }
            if($countad == '') {
                $countad = 0;
            }
            if($countenf == '') {
                $countenf = 0;
            }
            if($count18_24 == '') {
                $count18_24 = 0;
            }

            $requete ="UPDATE organisations SET 
			n_fam='".$countactif."',
			n_pers='".$countmember."',
			n_nour='".$countnourisson."',
			n_bebe='".$countbebe."',
			n_enf='".$countenf."',
			n_ado='".$countad."',
			n_18_24='".$count18_24."',
			n_sen='".$countsenior."',
			n_eq='".number_format($counteq, 2, '.', '')."' WHERE id_dis=".$datadis->id_dis."";
            $resultat=mysqli_query($connection,$requete);
            if (!$resultat)
            {
                echo $requete ;
                echo '<br>Il y a une erreur dans le programme 4 compte.php. Veuillez esasayer plus tard'. mysqli_error($connection);
            }
            else
            {
                $requete ="INSERT INTO population (date_stat,id_dis,n_fam,n_pers,n_nour,n_bebe,n_enf,n_ado,n_18_24,n_eq,lien_dep,n_sen,lien_banque) VALUES('".$mydate."','".$datadis->id_dis."','".$countactif."','".$countmember."','".$countnourisson."','".$countbebe."','".$countenf."','".$countad."','".$count18_24."','".number_format($counteq, 2, ',', ' ')."','".$ld."','".$countsenior."','".$lb."')";

                $resultat=mysqli_query($connection,$requete);
                if (!$resultat)
                {
                    echo $requete;
                    echo '<br>Il y a une erreur dans l\'insert 4 en population. Veuillez essayer plus tard'. mysqli_error($connection);
                }
                else
                {
                    echo "Statistiques mises &agrave; jour pour insert 4.";
                }
            }

            break;
    } // end switch
} //end while 1

////////////////////////////////////////////////////////////////////
// - le code suivant évalue la date de fin de suspension d'une organisation et annule la suspension 1 jour avant la date de fin.
$req="select stop_susp,id_dis from organisations where stop_susp <>' '";
$res=mysqli_query($connection,$req);
while ($data=mysqli_fetch_object($res))
{
    $today=0;
    $dn=0;
    $days_susp=0;
    $id=$data->id_dis;
    $today = mktime(0,0,0,date("m" ),date("d" ),date("Y" ));
    $stopsusp = explode("/",$data->stop_susp);
    $dn=mktime(0,0,0,$stopsusp[1].("m"),$stopsusp[0].("d") ,$stopsusp[2].("Y" ));
    //echo '$dn='.$dn;'<br>';
    //echo '$today ='.$today.'&nbsp;&nbsp;<br>';
    $days_susp=($dn-$today)/100000;
    //echo '$days_susp='.$days_susp;
    $daysus=round($days_susp);
    //echo '$daysus ='.$daysus;
    if ($daysus < 2) {
        $req2=mysqli_query($connection,"UPDATE organisations SET stop_susp = ' ', susp = 0 WHERE id_dis =".$id);
    }
}

///// le code suivant enlève les espaces blancs dans le champ n_eq
$result = mysqli_query($connection,"UPDATE `population` SET `n_eq` = REPLACE(`n_eq`, ' ', '')");
    break;
}
$message = "Update was successful";
if ($errormsg != "")
{
    $message = "Error: " . $errormsg;
}
$insertQuery_audit_daily = "INSERT INTO `auditchanges` (user,bank_id,id_dis,entity,entity_key,action) 
 VALUES ('avdmadmin',10,0,'population','" . substr($message,0,50) . "','Update')";
$sql = $connection->query($insertQuery_audit_daily);
if (!$sql)
{
    $errormsg=  "Failed to execute insert daily audit  query: " . $connection->error;
    echo "movements summarize.php failed to insert daily statistics in auditchanges table:" . $errormsg . "\n";
}


$connection->close();
echo "movements summarize.php ended at " . date("Y-m-d H:i:s") . "\n";
?>